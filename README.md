# Daikon Lambda

![Daikon](./logo.svg)

Use Daikon Routing in your AWS Lambda

## How to add Daikon Lambda to your project
[![](https://jitpack.io/v/daikonweb/daikon-lambda.svg)](https://jitpack.io/#daikonweb/daikon-lambda)

### Gradle
- Add JitPack in your root build.gradle at the end of repositories:
```
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

- Add the dependency
```
implementation 'com.github.DaikonWeb:daikon-lambda:0.1.0'
```

### Maven
- Add the JitPack repository to your build file 
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
- Add the dependency
```
<dependency>
    <groupId>com.github.DaikonWeb</groupId>
    <artifactId>daikon-lambda</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Getting Started
```
class MyHandler : HttpHandler() {
    override fun routing() {
        get("/") { req, res -> res.write("Hello, I'm a daikon-lambda!") }
    }
}
```

## Resources
* Documentation: https://daikonweb.github.io
* Examples: https://github.com/DaikonWeb/daikon-examples

## Authors

* **[Marco Fracassi](https://github.com/fracassi-marco)**
* **[Alessio Coser](https://github.com/AlessioCoser)**

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details
