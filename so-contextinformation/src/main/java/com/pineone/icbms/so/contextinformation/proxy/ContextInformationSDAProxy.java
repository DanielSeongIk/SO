package com.pineone.icbms.so.contextinformation.proxy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pineone.icbms.so.contextinformation.entity.ContextInformation;
import com.pineone.icbms.so.util.address.AddressStore;
import com.pineone.icbms.so.util.address.ContextAddress;
import com.pineone.icbms.so.util.conversion.DataConversion;
import com.pineone.icbms.so.util.http.ClientService;
import com.pineone.icbms.so.util.logprint.LogPrint;
import com.withwiz.beach.network.http.message.IHttpResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by melvin on 2016. 8. 1..
 * NOTE: ContextInformationLogic Component 에서 제공하는 데이터에 접근하거나 데이터를 이용해서 외부와 연동
 */

@Service
public class ContextInformationSDAProxy implements ContextInformationProxy{

    @Autowired
    ClientService clientService;

    @Autowired
    ContextAddress contextAddress ;
    public static ContextInformationSDAProxy newContextInformationProxy(){
        return new ContextInformationSDAProxy();
    }

    public static final Logger logger = LoggerFactory.getLogger(ContextInformationSDAProxy.class);

    //NOTE: SDA 에 ContextInformationLogic 등록 TODO: Response 논의
    public String registerContextInformation(ContextInformation contextInformation){
        //
        String sendData = DataConversion.objectToString(contextInformation);
        logger.info(LogPrint.outputInfoLogPrint());
        logger.debug("ContextInformation = " + contextInformation.toString());
//        contextAddress = ContextAddress.newContextAddress();
        IHttpResponseMessage message = clientService.requestPostService
                (contextAddress.getServerAddress(ContextAddress.SDA_SERVER) + AddressStore.REGISTER_CONTEXTINFORMATION, sendData);
        String response = new Gson().toJson(message);
        return response;
    }

    //NOTE : SDA 에서 ContextInformationList 조회
    public List<String> retrieveContextInformationListFromSDA(){
        //
        logger.info(LogPrint.outputInfoLogPrint());
        IHttpResponseMessage message = clientService.requestGetService(
                contextAddress.getServerAddress(ContextAddress.SDA_SERVER) + AddressStore.RETRIEVE_CONTEXTINFORMATION);
        String readData = new Gson().toJson(message);
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> contextInformationNameList = new Gson().fromJson(readData,type);
        return contextInformationNameList;
    }

    //NOTE : SDA 의 ContextInformationLogic 상세 조회
    public ContextInformation retrieveGeneralContextDetail(String contextId){
        //
        logger.info(LogPrint.outputInfoLogPrint());
        logger.debug("ContextId = " + contextId);
        IHttpResponseMessage message = clientService.requestGetService(
                contextAddress.getServerAddress(ContextAddress.SDA_SERVER) + AddressStore.RETRIEVE_CONTEXTINFORMATION + "/" + contextId);
        String readData = new Gson().toJson(message);
        Type type = new TypeToken<ContextInformation>(){}.getType();
        ContextInformation contextInformation = new Gson().fromJson(readData,type);
        return contextInformation;
    }
}
