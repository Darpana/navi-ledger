package navi.service;

import navi.dto.response.BaseResponse;

import java.util.regex.Matcher;

public abstract class BankingRequest {
    public abstract String getType();

    public abstract void setParameter(Matcher matcher) throws Exception;

    public abstract BaseResponse processInput() throws Exception;
}
