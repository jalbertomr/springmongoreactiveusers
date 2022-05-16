# web application REST interface to manage users (Reactive)

###Dependencies

 Starting with this dependencies

* Spring DataReactive MongoDB
* Embedded MongoDB
* Spring Boot Actuator
* Spring Reactive Web
* Spring Boot DevTools

* [jalbertomr.blogspot.com] (http://jalbertomr.blogspot.com)


We has been using till the moment a Repository that extends ReactiveCRUDRepository and ReactiveQueryByExampleExecutor<User>
so we can not autogenerate de IDs of the mongo database so in order to evaluate another Repository we
creating a Repository IUserMongoRepository

#### Controller

    @GetMapping
	 public Flux<User> getUsers(){
		return userRepository.findAll();
    }

With data

    Status Code:  200 ok
    Body:
    [
    {
        "id": 2,
        "name": "SimpleProduct1",
        "email": "simple1@email.com",
        "password": "simplepassword1",
        "roles": [
            "ADMIN",
            "USER"
        ],
        "lastLogin": "2022-05-15T23:34:00.340Z",
        "enabled": false
    }
    ]

With out data

    Status Code: 200 ok
    Body:
    []


Integrating the response code into the body, and always the response code is 200 ok, not practical.

    @GetMapping
	 public Flux<?> getUsers(){
		return userRepository.findAll()
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	 }
	 
This returns a flux

With out data 

    Status Code:  200 Ok
    Body:
    [
    {
        "headers": {},
        "body": null,
        "statusCode": "NOT_FOUND",
        "statusCodeValue": 404
    }
    ]	 

With data

    Status Code:  200 Ok
    Body:
    [
    {
        "headers": {},
        "body": {
            "id": 1,
            "name": "SimpleProduct1",
            "email": "simple1@email.com",
            "password": "simplepassword1",
            "roles": [
                "ADMIN",
                "USER"
            ],
            "lastLogin": "2022-05-15T23:23:25.589Z",
            "enabled": false
        },
        "statusCode": "OK",
        "statusCodeValue": 200
    }
    ]
    



