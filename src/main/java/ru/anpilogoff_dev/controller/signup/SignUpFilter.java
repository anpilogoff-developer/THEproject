package ru.anpilogoff_dev.controller.signup;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.anpilogoff_dev.database.model.UserDataObject;
import ru.anpilogoff_dev.database.model.UserModel;
import ru.anpilogoff_dev.service.SignUpService;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Фильтр, перехватывающий запросы к странице регистрации.
 * Инициализирует валидатор и обрабатывает GET и POST запросы.
 */
public class SignUpFilter implements Filter {
    private static final Logger log = LogManager.getLogger("RuntimeLogger");
    private static final Logger logger = LogManager.getLogger(SignUpFilter.class);

    /**
     * Предоставляет методы для валидации объектов Java Bean согласно ограничениям, определенным аннотациями валидации.
     */
    private Validator validator;

    /**
     * Получение экземпляра ValidatorFactory из атрибутов контекста сервлета и последующее создание валидатора.
     */
    @Override
    public void init(FilterConfig filterConfig) {
        ValidatorFactory factory = (ValidatorFactory) filterConfig.getServletContext().getAttribute("factory");
        this.validator = factory.getValidator();
    }

    /**
     * Метод фильтрации HTTP-запросов для регистрации пользователя.
     * Перенаправляет уже авторизованных пользователей на главную страницу.
     * Обрабатывает GET-запросы для подтверждения регистрации и POST-запросы для регистрации пользователя.
     * @param servletRequest  Запрос к сервлету.
     * @param servletResponse Ответ сервлета.
     * @param filterChain     Цепочка фильтров.
     * @throws IOException      В случае ошибки ввода/вывода.
     * @throws ServletException В случае ошибки сервлета.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String method = req.getMethod();

        if (req.getSession(false) != null && req.getHeader("Authorization") != null) {
            resp.sendRedirect(req.getServletContext().getContextPath() + "/home");
        } else if (method.equals("GET")) {
            String requestParamsString = req.getQueryString();
            if (requestParamsString != null && requestParamsString.contains("confirmation")) {
                String param = req.getParameter("confirmation");
                if (param == null || param.isEmpty()) {
                    req.getRequestDispatcher("signup.html").forward(req, resp);
                    return;
                }
            }
            filterChain.doFilter(req, resp);
        } else if (method.equals("POST")) {
            Writer writer = resp.getWriter();

            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");

            List<String> params = new ArrayList<>();

            boolean allParamsNotEmpty = Collections.list(req.getParameterNames())
                    .stream()
                    .allMatch(param -> {
                        String paramVal = req.getParameter(param);
                        if (paramVal != null && !paramVal.isEmpty()) {
                            params.add(paramVal);
                            return true;
                        }
                        return false;
                    });

            if (allParamsNotEmpty) {
                UserModel model = new UserModel(params.get(0), params.get(1), params.get(2), params.get(3));
                //валидация параметров для регистрации
                JSONObject paramsValidationErrors = validateParams(model);

                if (paramsValidationErrors != null) {
                    writer.write(paramsValidationErrors.toString());
                    writer.flush();
                    writer.close();
                } else {
                    SignUpService service = (SignUpService) req.getServletContext().getAttribute("userDataService");
                    //проверка существования пользователя
                    UserDataObject isExist = service.checkIsUserExist(model);

                    if (isExist != null) {
                        JSONObject alreadyExistJsonResponse = new JSONObject();
                        alreadyExistJsonResponse.put("success", false);
                        alreadyExistJsonResponse.put("valid", true);

                        switch (isExist.getRegistrationStatus()) {
                            case LOGIN_EXISTS:
                                alreadyExistJsonResponse.put("reason", "user with same login already exists");
                                break;
                            case EMAIL_EXISTS:
                                alreadyExistJsonResponse.put("reason", "user with same email already exists");
                                break;
                            case NICKNAME_EXISTS:
                                alreadyExistJsonResponse.put("reason", "user with same nickname already exists");
                                break;
                            case UNCONFIRMED:
                                alreadyExistJsonResponse.put("reason", "unconfirmed");
                                break;
                            case CONFIRMED:
                                alreadyExistJsonResponse.put("reason", "existed_user_data");
                        }
                        writer.write(alreadyExistJsonResponse.toString());
                        writer.flush();
                        writer.close();
                    } else {
                        log.debug("SignupFilter.doFilter():  user  DOESN'T NOT EXISTS");
                        filterChain.doFilter(servletRequest, servletResponse);
                    }
                }
            }
        }
    }


       /**
     * Валидирует параметры пользователя, используя Bean Validation API.
     * В случае обнаружения ошибок валидации, создает и возвращает JSON объект с деталями ошибок.
     *
     * @param model Модель пользователя, содержащая данные для валидации
        * @see UserModel
     * @return JSONObject с результатами валидации или null, если ошибок нет.
     */
    JSONObject validateParams(UserModel model) {
        logger.debug("@SignupFilter.doFilter()>> validateParams()");
        Set<ConstraintViolation<UserModel>> violations = validator.validate(model);

        JSONObject validationError = null;
        if (!violations.isEmpty()) {
            JSONArray errors = new JSONArray();

            for (ConstraintViolation<UserModel> violation : violations) {
                log.debug("   --validator: invalid value:   " + violation.getMessage() + "\n");

                JSONObject error = new JSONObject();
                error.put("parameter", violation.getPropertyPath().toString());
                error.put("message", violation.getMessage());
                errors.put(error);
            }
            validationError = new JSONObject();
            validationError.put("success", false);
            validationError.put("valid", false);
            validationError.put("errors", errors);
        }
        return validationError;
    }
}


