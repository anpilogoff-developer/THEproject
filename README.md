# The Project



## Target: create a Web Client-Server Application

**Objective:** Develop a web client-server application with the following features:
1. **Text Chat (live):**
    - Implement a live text chat feature.

2. **Private Messages:**
    - Enable private messaging between users.

3. **Audio Calls:**
    - Implement audio calls with:
      - Voice filters.
      - Individual and group calls.

4. **Online Player:**
    - Create an online music player with functionalities like:
      - Music search by genre, title, and other categories.
      - Identification of unknown tracks (from file or microphone).
      - Ability to set marks on the time bar.
      - Playlist creation and editing.
      - Track download (in specified format).
      - Online live radio.

Deploy the application on a VPS with Ubuntu 22.04 and the domain anpilogoff-dev.ru.

1. The application is deployed in a local instance of Apache Tomcat 9.0.8 servlet container on a VPS, awaiting requests on port 80.
2. External requests targeting specific servlet containers are directed to port 443 and proxied to Tomcat on port 80 using Nginx.


## Technologies(will be expand in project progress)

- **Java:11**
- **Maven**
- **JavaScript:**
- **SQL**
- **Git**
- **HTML**





## Project Structure(will modiаfied later)

```plaintext
├── src/                           
│   ├── main/                      
│   │   ├── java/                  
│   │   ├── resources/             
│   │   └── webapp/                
│   │      └── WEB-INF/
│   └── test/                      
│       ├── java/                  
├── target/                        
├── pom.xml                        
├── .gitignore                     
└── README.md



```

# Project Plan

<h2> Step 1. Настройка окружения </h2>  


<details>  
  
  <summary>1. Установка Java 11:</summary>  
  

   
   ```bash
      sudo apt update  
      sudo apt install openjdk-11-jdk
  ```
-  открываем файл `~/.bashrc`
-  Добавлем в конец:  
    ```bash
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
    export PATH=$PATH:$JAVA_HOME/bin
    ```
- сохраняем изменения и закрываем файл.
- чтобы обновить переменную PATH без перезагрузки системы выполняем команду:

  ```bash
  source ~/.bashrc
  ```
- проверяем всё ли прошло успешно командой:
  ```bash
  java --version
  ```
</details>

<details>
<summary>2. Установка Maven и добавление в переменную PATH</summary>  

```bash
  sudo apt install maven
  ```
- Добавляем переменные окружения `M2_HOME` и `M2` помещая в тот же файл `~/.bashrc`  строки:
  ```bash
  export M2_HOME=/usr/share/maven
  export M2=$M2_HOME/bin
  export PATH=$PATH:$M2_HOME/bin
  ```
- Сохраняем изменения, закрываем файл и выполняем:
  ```bash
  source ~/.bashrc
  ```

- Проверяем всё ли прошло успешно командой:
  ```bash
  mvn  --v
  ```
</details>

<details>
<summary>3. Установка и настройка Apache Tomcat 9</summary>

- с оф. сайта скачаиваем Tomcat:
   ```bash
   wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.85/bin/apache-tomcat-9.0.85.tar.gz)
   ```
- создаём каталог `/opt/tomcat`:  
    ```bash
    sudo mkdir /opt/tomcat
    ```
- распаковываем в него скачанный архив 
  ```bash
  sudo tar xzvf apache-tomcat-*tar.gz -C /opt/tomcat --strip-components=1
  ```
- не рекомендуется запускать контейнер от root пользователя, по этому создаём  группу tomcat и пользователя 'tomcat':  
    
  ```bash
      sudo groupadd tomcat
      sudo useradd -s /bin/false -g tomcat -d /opt/tomcat tomcat  
      # -s /bin/false -  это специальная оболочка, которая обычно используется для системных учетных записей 
      #  или сервисных аккаунтов. Установка оболочки в /bin/false означает, что пользователь не сможет войти в систему интерактивно,
      #  но его учетная запись может использоваться для запуска определенных процессов или служб
      # -g - группа
      # -d - домашний каталог
    ```
- Предоставляем группе tomcat право владения каталогом /opt/tomcat
    ```bash
      sudo chgrp -R tomcat /opt/tomcat
    ```
- я буду запускать Tomcat как службу - по этой причине переменные окружения CATALINA_HOME/BASE будут объявлены в файле tomcat.service(в следующем шаге). В ином случае добавляем переменные среды для Tomcat `~/.bashrc`:
  ```bash
  echo 'export CATALINA_HOME=/opt/tomcat' >> ~/.bashrc
  echo 'export PATH=$PATH:$CATALINA_HOME/bin' >> ~/.bashrc
  ```
- Создаём файл службы `tomcat.service` в каталоге `/etc/systemd/system/`:
    ```bash
    sudo nano /etc/systemd/system/tomcat.service
    ```
- Добавляем в файл `tomcat.service`  следующее содержимое :  
    ```bash
    [Unit]
    Description=Apache Tomcat Web Application Container
    After=network.target

    [Service]
    Type=forking
    Environment=JAVA_HOME=/usr/lib/jvm/java-1.11.0-openjdk-amd64
    Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
    Environment=CATALINA_HOME=/opt/tomcat
    Environment=CATALINA_BASE=/opt/tomcat
    
    #Устанавливает режим "без графического интерфейса" для Java AWT, что полезно для серверных приложений, которые не требуют графического вывода.
    Environment="CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC"
    
    # Устанавливает источник энтропии для генерации случайных чисел в Java.
    Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'


    ExecStart=/opt/tomcat/bin/startup.sh
    ExecStop=/opt/tomcat/bin/shutdown.sh

    User=tomcat
    Group=tomcat
    RestartSec=10
    Restart=always

    [Install]
    WantedBy=multi-user.target
    ```
- После создания файла службы перезагружаем systemd:
    ```bash
      sudo systemctl daemon-reload
    ```
- Открываем файл `tomcat-users.xml`, в каталоге конфигурации Tomcat:  
    ```bash
    sudo nano /opt/tomcat/conf/tomcat-users.xml
    ```
- Добавляем необходимые настройки пользователей, например:
  ```xml
  <tomcat-users>
    <user username="tomcat" password="tomcat" roles="manager-gui,admin-gui,manager-script,manager-jmx"/>
  </tomcat-users>
  ```
</details>

<details>
    <summary>4. Установка Mysql 8</summary>

- Для установки выполняем в терминале:
   ```bash
   sudo apt install mysql-server
   ```
- В случае успешной установки MySQL сервер будет запущен автоматически;
  сий факт можно проверить выполнив команду:

    ```bash
      sudo service mysql status
    ```  
  Вышеописанные действия не  являться не можетпредложат вам установить пароль или внести какие-либо изменения в
   конфигурационные файлы. В этой связи - работа со свежеустановленным MySQL безопасной являться не может
    , так что следующий подпункт посодействует в решении этого вопроса...

- Запускаем сценарий:
    ```bash
      sudo mysql-secure-installation
    ```
  - после этого будет:
    - удалёна анонимная учётная запись;
    - отключён удаённый доступ для пользоватля root;
    - удалены тестовые базы данных и таблицы в них;
  

- На официальном сайте Ubuntu советуют также установить плагин MySQL Tuner для мониторинга активности 
  MySQl в райнтайме и получения советов по её оптимизации(такого ранее не делал, по этому попробую); 
    ```bash
      sudo apt install mysqltuner
    ```

</details>

<details>
<summary>5. Установка Nginx</summary>  

- для локального запуска есть смысл установить с целью исключения названия приложения из URI запроса(чтобы вместо localhost:8080/myapp был localhost/),
    однако в  случае с VPS все запросы будут приходить по https на порт 443. По этой причине устанавливаем по обычной схеме: 
  ```bash
  sudo apt install nginx
  ```
- для локального запуска с целью сокращения uri запроса содержимое файла /etc/nginx/sites-available/default будет следующим:
    ```bash
    server {
      listen 80 default_server;
      listen [::]:80 default_server;
      
      location / {
          proxy_pass http://127.0.0.1:8080/webappname;
      }
    }
    ```  
- в случае с VPS запросы https # нужно проксировать на порт 8080 на 
  котором по незащищённому соединению http на порту 8080 слушает Tomcat необходимо создать файл название которого соответствует доменному имени
  в моём случае* /etc/nginx/sites-available/anpilogoff-dev.ru со следующим содержимым:
  ```bash
  server {
    listen 443 ssl;
    server_name anpilogoff-dev.ru www.anpilogoff-dev.ru;

    ssl_certificate /etc/nginx/ssl/cert.crt; #расположение публичного сертификата
    ssl_certificate_key /etc/nginx/ssl/ssl_private_key_unencrypted.key; #расположение приватного ключа для домена anpilogoff-dev.ru

    location / {
         proxy_pass http://127.0.0.1:8080/THEproject/;
    }
  }
  ```
  
- Для отключения функции автозапуска nginx при загрузке(turn on) сервера  если необходимо выполняем:
    ```bash
      sudo systemctl disable nginx  
      #соответственно 'enable' дабы достичь обратного результата
    ```  
  
</details>

<details>
<summary>6. Установка сертификата на VPS</summary>

- Для обеспечения безопасного клиент-серверного взаимодействия с условием обязательного шифрования данных 
  при отправке с обеих сторон, необходимо установить SSL/TLS-сертификат для домена на сервере.
  В масштабах моего текущего понимания этого вопроса, самым простым способом это реализовать
  -использовать утилиту Certbot, которая работает с центром сертификации Let's Encrypt.

- Устанавливаем Certbot с плагином Nginx если таковой по умолчанию отсутствует:
    ```bash
      sudo apt install certbot python3-certbot-nginx
    ```
- Получаем SSL-Сертификат:
    ```bash
      sudo certbot --nginx -d anpilogoff-dev.ru -d www.anpilogoff-dev.ru
      # --nginx этот плагин автоматически подкорректирует настройки nginx
      # -d - доменное имя для которого нужен сертфикат   
    ```

- Сертификаты Let's Encrypt действительны в течение девяноста дней, certbot, установленный в предыдущем шаге
  автоматически добавляет таймер (certbot.timer) как сервис, который автоматически 2 раза в день
  будет будет запускаться 2 раза в день и в случае необходимости обновлять сертификаты, остаточный срок действия
  которых будет 30 дней или меньше. Статус таймера проверяется командой:

    ```bash
    ```  
</details>
<details>  
<summary>7.Отправка e-mail</summary>  

- Устанавливаю "Postfix"(Mail Transfer Agent):  

  ```bash
    sudo apt install postfix
  ```  
  
- Вносим изменения в файл /etc/postfix/main.cf:
  ```bash
    smtp_tls_CApath = /etc/ssl/certs  
             Указывает путь к директории, содержащей доверенные CA сертификаты,
             используемые для проверки сертификатов серверов при установлении исходящих соединений.
  
    smtp_tls_security_level=encrypt  
             Устанавливает уровень безопасности TLS для исходящих соединений на "encrypt", требуя шифрование ВСЕХ соединений 
    
     smtp_tls_session_cache_database = btree:${data_directory}/smtp_scache  
             Определяет базу данных для кэширования параметров сессии TLS исходящих соединений, 
             что может ускорить повторные соединения.  
    
     myhostname = anpilogoff-dev.ru: Указывает имя хоста почтового сервера.  
  
    inet_interfaces = loopback-only  
            "loopback-only" если есть необходимость использовать сервер только для отправки исходящей почты, all - наоборот.  
    
    relayhost =    
             Отсутствие значения указывает, что сервер напрямую доставляет исходящую почту, без использования внешнего ретранслятора.  
    
    
  ```
</details>





