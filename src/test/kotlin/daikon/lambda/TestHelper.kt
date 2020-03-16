package daikon.lambda

import com.google.gson.Gson
import io.mockk.mockk
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream

fun runHandler(input: InputStream, output: OutputStream, routes: HttpHandler.() -> Unit) {
    return object : HttpHandler() {
        override fun routing() {
            routes()
        }
    }.handleRequest(input, output, mockk())
}

fun apiGatewayEvent(
        method: String,
        path: String,
        queryParams: Map<String, String> = emptyMap(),
        body: String? = null,
        headers: Map<String, String> = emptyMap(),
        domain: String? = null
): InputStream {
    val event = """{
            "resource": "$path",
            "path": "$path",
            "httpMethod": "$method",
            "headers": ${Gson().toJson(headers)},
            "multiValueHeaders": {
                "Accept": [
                    "*/*"
                ],
                "CloudFront-Forwarded-Proto": [
                    "https"
                ],
                "CloudFront-Is-Desktop-Viewer": [
                    "true"
                ],
                "CloudFront-Is-Mobile-Viewer": [
                    "false"
                ],
                "CloudFront-Is-SmartTV-Viewer": [
                    "false"
                ],
                "CloudFront-Is-Tablet-Viewer": [
                    "false"
                ],
                "CloudFront-Viewer-Country": [
                    "IT"
                ],
                "Host": [
                    "fxjlqdsdya.execute-api.eu-west-1.amazonaws.com"
                ],
                "User-Agent": [
                    "curl/7.64.1"
                ],
                "Via": [
                    "2.0 4272985387a50d2af0b808fc13483a80.cloudfront.net (CloudFront)"
                ],
                "X-Amz-Cf-Id": [
                    "ATLMhVJ3TaNE-p-w5kcvv17Pf5I6j9lIi2NYVDBZwGgyQODSCqt4vA=="
                ],
                "X-Amzn-Trace-Id": [
                    "Root=1-5e6f3c19-98089ca85752e71c36bb5370"
                ],
                "X-Forwarded-For": [
                    "82.50.91.88, 70.132.10.79"
                ],
                "X-Forwarded-Port": [
                    "443"
                ],
                "X-Forwarded-Proto": [
                    "https"
                ]
            },
            "queryStringParameters": ${Gson().toJson(queryParams)},
            "multiValueQueryStringParameters": {},
            "pathParameters": {},
            "stageVariables": null,
            "requestContext": {
                "resourceId": "ceorqw",
                "resourcePath": "$path",
                "httpMethod": "$method",
                "extendedRequestId": "JeZUBFh2DoEF4Gg=",
                "requestTime": "16/Mar/2020:08:43:05 +0000",
                "path": "/dev/$path",
                "accountId": "684411073013",
                "protocol": "HTTP/1.1",
                "stage": "dev",
                "domainPrefix": "fxjlqdsdya",
                "requestTimeEpoch": 1584348185670,
                "requestId": "e4253a4a-1b89-4189-8618-aaaf69bc77d3",
                "identity": {
                    "cognitoIdentityPoolId": null,
                    "accountId": null,
                    "cognitoIdentityId": null,
                    "caller": null,
                    "sourceIp": "82.50.91.88",
                    "principalOrgId": null,
                    "accessKey": null,
                    "cognitoAuthenticationType": null,
                    "cognitoAuthenticationProvider": null,
                    "userArn": null,
                    "userAgent": "curl/7.64.1",
                    "user": null
                },
                ${if(domain != null) """"domainName": "$domain",""" else ""}
                "apiId": "fxjlqdsdya"
            },
            "body": ${if (body != null) """"$body"""" else "null"},
            "isBase64Encoded": false
        }"""
    return streamOf(event)
}

fun streamOf(text: String) = ByteArrayInputStream(text.toByteArray())