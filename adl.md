# Architecture Decision log

### 1. Selenium related package
All classes that uses import from `org.selenium.*` classes should be only in package: `dev.comfast.se.*`. 
Use imports from `org.selenium.*` in other packages is prohibited. 
This approach will allow to add different backend client in future like playwright, puppeteer or direct CDP/WebDriver client.