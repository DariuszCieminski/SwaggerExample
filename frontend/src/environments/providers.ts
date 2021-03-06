import { HTTP_INTERCEPTORS, HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { NgxPermissionsService } from "ngx-permissions";
import { AuthenticationGuard } from "../app/main/services/authentication/authentication-guard";
import { AuthenticationService } from "../app/main/services/authentication/authentication-service";
import { JwtAuthenticationGuard } from "../app/main/services/authentication/jwt/jwt-authentication-guard";
import { JwtAuthenticationService } from "../app/main/services/authentication/jwt/jwt-authentication.service";
import { RequestInterceptor } from "../app/main/services/authentication/jwt/request.interceptor";

export const PROVIDERS = [
    {
        provide: AuthenticationService,
        useClass: JwtAuthenticationService,
        deps: [HttpClient, NgxPermissionsService]
    },
    {
        provide: AuthenticationGuard,
        useClass: JwtAuthenticationGuard,
        deps: [JwtAuthenticationService, Router]
    },
    {
        provide: HTTP_INTERCEPTORS,
        useClass: RequestInterceptor,
        deps: [JwtAuthenticationService, Router],
        multi: true
    }
];