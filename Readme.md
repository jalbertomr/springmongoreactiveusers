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

###### findAll with limit

    public Flux<User> findAllLimit(@RequestParam(name = "limit", required = false, defaultValue="-1")       long limit ) {
		if (limit == -1) {
			return userRepository.findAll();
		};
		return userRepository.findAll().take(limit);
	 };

###### findUserByExample

Using matchers the search can require some part, exact part,... of the fields 

	public Mono<User> findUserbyExample(User user) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase()
		.withMatcher("email", GenericPropertyMatcher::contains)
		.withMatcher("role",  GenericPropertyMatcher::contains)
		.withMatcher("enabled", GenericPropertyMatcher::exact);
		Example<User> example = Example.of(user, matcher);
		return userRepository.findOne(example);
	}
    
#### DataBase Initializing

The initializing of the database is using a bean, a class that implements the SmartInitilizingSingleton overriding the afterSingletonInstantiated method.

#### Validation with JSR-303, (javax.validation)

 To validate the input data at controller level, add the dependency
 
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
 
Put the respective annotations on the field of the entity class

    ...
    __@NotEmpty @NotNull(message="email must be supplied")__
	 @Builder.Default
	 private String email = "";
	
    __@Size(min=8,max=254)__
    private String password = "";
	 ...

In the controller must be the @Valid annotation in the parameter area of the function

    @PostMapping
	 public Mono<ResponseEntity<User>> newUserMono(__@Valid__ @RequestBody Mono<User> userMono,    ServerHttpRequest req) {
	 ...


#### Custom Validator

Define the annotation for the roles validator

	@Target({ ElementType.FIELD})
	@Retention(RetentionPolicy.RUNTIME)
	@Constraint(validatedBy = {ValidRolesValidator.class})
	@Documented
	public @interface ValidRoles {
		String message() default "invalid role detected";
		Class<?>[] groups() default {};
		Class<? extends Payload>[] payload() default {};
	}

Define the behavior of the validator

    public class ValidRolesValidator implements ConstraintValidator<ValidRoles, Collection<String>> {
	
	 private List<String> validRoles = Arrays.asList("ROLE_USER","ROLE_ADMIN");
	
	   @Override
	   public boolean isValid(Collection<String> collection, ConstraintValidatorContext context) {
        return collection.stream().allMatch(validRoles::contains);
	   }
    }

finally put the role validator in the field of the entity

	public class User {
	    ...
	    @ValidRoles
		private List<String> roles = new ArrayList<>();
		...
	}
		 