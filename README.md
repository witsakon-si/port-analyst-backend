## Demo Project URL: http://54.252.178.254 (Deploy on AWS EC2)
### Feature
   - Price Alert
      + Connect socket finnhub.io -> store current price 
      + Display realtime price / CRUD price alert condition / Notify according condition
   - Portfolio
      + CRUD Trading History / Dividend
      + CRUD Cash In/Out
      + CRUD Note
      + Calculate profit/loss, Show summary port, Chart U.P/L history
      + Get graph from investing.com and show
      + Show perfomance by year, month, week
### Technology
   - Spring Boot (:8080)
      + JPA/JDBC
      + MVC/CRUD Service
      + Socket, HTML Scrapping
      + Hazelcast Caching
   - Angular (:80)
      + PrimeNg
      + Deploy on Nginx
    
### Sonarqube
```
docker-compose up -d
```
- Goto http://localhost:9000 (first login use admin/admin)
- Crete local project select maven then run command
```
mvn clean verify sonar:sonar  \
  -Dsonar.projectKey=<project_name>  \
  -Dsonar.projectName='<project_name>'  \
  -Dsonar.host.url=http://localhost:9000  \
  -Dsonar.token=<token>
```
- Let's fixing
