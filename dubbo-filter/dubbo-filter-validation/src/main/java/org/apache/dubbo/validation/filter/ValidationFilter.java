/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.validation.filter;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.validation.Validation;
import org.apache.dubbo.validation.Validator;

import javax.validation.ValidationException;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;
import static org.apache.dubbo.common.constants.FilterConstants.VALIDATION_KEY;

/**
 * ValidationFilter invoke the validation by finding the right {@link Validator} instance based on the
 * configured <b>validation</b> attribute value of invoker url before the actual method invocation.
 *
 * xjh-ValidationFilter将会基于url的validation参数找到适合的Validator实例，来进行参数校验
 * 它的主要作用是通过结合JSR 303 Bean Validation规范（如Hibernate Validator）对服务方法的输入参数进行验证，从而确保参数的合法性和数据的完整性。
 * 如@NotNull, @Size, @Min, @Max等
 *
 * <pre>
 *     e.g. &lt;dubbo:method name="save" validation="jvalidation" /&gt;
 *     In the above configuration a validation has been configured of type jvalidation. On invocation of method <b>save</b>
 *     dubbo will invoke {@link org.apache.dubbo.validation.support.jvalidation.JValidator}
 * </pre>
 *
 * To add a new type of validation
 * <pre>
 *     e.g. &lt;dubbo:method name="save" validation="special" /&gt;
 *     where "special" is representing a validator for special character.
 * </pre>
 *
 * developer needs to do
 * <br/>
 * 1)Implement a SpecialValidation.java class (package name xxx.yyy.zzz) either by implementing {@link Validation} or extending {@link org.apache.dubbo.validation.support.AbstractValidation} <br/>
 * 2)Implement a SpecialValidator.java class (package name xxx.yyy.zzz) <br/>
 * 3)Add an entry <b>special</b>=<b>xxx.yyy.zzz.SpecialValidation</b> under <b>META-INF folders org.apache.dubbo.validation.Validation file</b>.
 *
 * xjh-自定义校验（参考默认实现JValidation）：1.新建一个类SpecialValidation实现Validation或者继承AbstractValidation 2.与SpecialValidation同一个目录新建一个类SpecialValidator实现Validator接口
 * 3.在META-INF文件夹下面的org.apache.dubbo.validation.Validation文件中写入我们的SpecialValidation
 *
 * @see Validation
 * @see Validator
 * @see Filter
 * @see org.apache.dubbo.validation.support.AbstractValidation
 */
@Activate(group = {CONSUMER, PROVIDER}, value = VALIDATION_KEY, order = 10000)
public class ValidationFilter implements Filter {

    private Validation validation;

    /**
     * Sets the validation instance for ValidationFilter
     * @param validation Validation instance injected by dubbo framework based on "validation" attribute value.
     */
    public void setValidation(Validation validation) {
        // xjh-设置校验实例
        this.validation = validation;
    }

    /**
     * Perform the validation of before invoking the actual method based on <b>validation</b> attribute value.
     * @param invoker    service
     * @param invocation invocation.
     * @return Method invocation result
     * @throws RpcException Throws RpcException if  validation failed or any other runtime exception occurred.
     */
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (validation != null && !invocation.getMethodName().startsWith("$")
                && ConfigUtils.isNotEmpty(invoker.getUrl().getMethodParameter(invocation.getMethodName(), VALIDATION_KEY))) {
            try {
                // xjh-获取validator并校验
                Validator validator = validation.getValidator(invoker.getUrl());
                if (validator != null) {
                    validator.validate(invocation.getMethodName(), invocation.getParameterTypes(), invocation.getArguments());
                }
            } catch (RpcException e) {
                throw e;
            } catch (ValidationException e) {
                // only use exception's message to avoid potential serialization issue
                return AsyncRpcResult.newDefaultAsyncResult(new ValidationException(e.getMessage()), invocation);
            } catch (Throwable t) {
                return AsyncRpcResult.newDefaultAsyncResult(t, invocation);
            }
        }
        return invoker.invoke(invocation);
    }

}
