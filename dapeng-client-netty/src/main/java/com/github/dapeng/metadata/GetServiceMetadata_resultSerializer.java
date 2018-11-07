package com.github.dapeng.metadata;

import com.github.dapeng.core.BeanSerializer;
import com.github.dapeng.core.SoaCode;
import com.github.dapeng.core.SoaException;
import com.github.dapeng.org.apache.thrift.TException;
import com.github.dapeng.org.apache.thrift.protocol.*;

/**
 * Created by tangliu on 2016/3/3.
 */
public class GetServiceMetadata_resultSerializer implements BeanSerializer<getServiceMetadata_result> {
    @Override
    public getServiceMetadata_result read( TProtocol iprot) throws TException {

        getServiceMetadata_result bean= new getServiceMetadata_result();
        TField schemeField;
        iprot.readStructBegin();

        while (true) {
            schemeField = iprot.readFieldBegin();
            if (schemeField.type == TType.STOP) {
                break;
            }

            switch (schemeField.id) {
                case 0:  //SUCCESS
                    if (schemeField.type == TType.STRING) {
                        bean.setSuccess(iprot.readString());
                    } else {
                        TProtocolUtil.skip(iprot, schemeField.type);
                    }
                    break;
                default:
                    TProtocolUtil.skip(iprot, schemeField.type);
            }
            iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        validate(bean);
        return bean;
    }

    @Override
    public void write(getServiceMetadata_result bean, TProtocol oprot) throws TException {

        validate(bean);
        oprot.writeStructBegin(new TStruct("getServiceMetadata_result"));

        oprot.writeFieldBegin(new TField("success", TType.STRING, (short) 0));
        oprot.writeString(bean.getSuccess());
        oprot.writeFieldEnd();

        oprot.writeFieldStop();
        oprot.writeStructEnd();
    }

    @Override
    public void validate(getServiceMetadata_result bean) throws TException {

        if (bean.getSuccess() == null)
            throw new SoaException(SoaCode.RespFieldNull, "success字段不允许为空");
    }

    @Override
    public String toString(getServiceMetadata_result bean) {
        return bean == null ? "null" : bean.toString();
    }
}
