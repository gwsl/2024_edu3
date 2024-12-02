package com.ict.edu3.jwt;

public class JwtResponse {
    // final 수정 불가
    private final String token ;

    public JwtResponse(String token){
        this.token = token;
    }
    public String getToken(){
        return token;
    }
}
