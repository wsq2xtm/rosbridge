package com.gary.ros.rosbridge.implementation;

import com.gary.ros.rosbridge.operation.Operation;

/**EventBus event entity,describe ros server response info
 */

public class OperationEvent {
    public String msg;
    public String id;
    public String name;
    public String op;


    public OperationEvent(Operation operation, String name, String content) {
        if(operation != null) {
            id = operation.id;
            op = operation.op;
        }
        this.name = name;
        msg = content;
    }
}
