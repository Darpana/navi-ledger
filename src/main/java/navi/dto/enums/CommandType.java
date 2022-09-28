package navi.dto.enums;

import lombok.Getter;

@Getter
public enum CommandType {
    LOAN("LOAN",6),
    BALANCE("BALANCE", 4),
    PAYMENT("PAYMENT", 5);

    private final String command;
    private final Integer length;

    CommandType(String command, Integer length){
        this.command = command;
        this.length = length;
    }
}
