# Mystic Mock Server

- Embedded MySQL
- Spring
- Hibernate

<h4>The Deafult Port is 1209</h4>
- Property mystic.port

# Start HTTP Mock Server
1. Define MysticRunner Bean</br>
   <h4>GRAILS</h4>
        mysticRunner(MysticRunner) { bean -></br>
          &nbsp;&nbsp;&nbsp;&nbsp;bean.scope = "singleton"</br>
          &nbsp;&nbsp;&nbsp;&nbsp;endpoint = "http://real_url.com"</br>
          &nbsp;&nbsp;&nbsp;&nbsp;folder = "some_folder"</br>
          &nbsp;&nbsp;&nbsp;&nbsp;bean.initMethod = 'init'</br>
       }</br>
    <h4>SPRING XML</h4>
    bean id="mysticRunner" class="com.mystic.server.MysticRunner" init-method="init" scope="singleton"</br>
    &nbsp;&nbsp;&nbsp;&nbsp;property name="endpoint" value="https://real_url.com" </br>
    &nbsp;&nbsp;&nbsp;&nbsp;property name="folder" value="some_folder" </br>
    /bean

<h4>If you need some help don't hesitate to contact me  ;)</h4>
leo.unsta@gmail.com
