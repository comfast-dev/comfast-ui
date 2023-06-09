# Architecture Decision log

### 1. Selenium imports
All classes that uses imports from `org.selenium.*` classes should be only in package: `dev.comfast.se.*`. 
Use imports from `org.selenium.*` in other packages is prohibited.