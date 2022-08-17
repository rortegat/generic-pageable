# Generic Pagination

### Pagination functionality provided by Spring Data JPA without querying databases. 


The **Pageable** object included in Spring Boot JPA dependency allow us to implement the pagination functionality converting this object in to SQL code and filter our resultSet so we can't use it out of JPA context.


This Utils class allow us to reuse and apply the Pageable attributes to a given list of objects without using any database. 


How to use it...

Create a pageUtil for the type of objects that you want to fetch in Pages 

```java
PageUtil<MyObjectDto> pageUtil = new PageUtil<>();
```


The **listToPage** method receives a list of objects and returns the requested Page of object.

```java
//Pageable object with options defined by the client (page, size and sort)
Pageable pageable...
        
//List of objects retrieved from an external data source
List<MyObjectDto> objectsList = externalApiService.getAllUsersData();

//Applying the pageable options to the object lists
Page<MyObjectDto> objectsPage = listToPage(objectsList, pageable);
```


**NOTE: Check the included unit tests to know more about the uses cases covered by this Utils class.**

Run PageUtils tests using Apache Maven

```command
mvn test -Dtest=PageUtilsTest
```
