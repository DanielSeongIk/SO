package com.pineone.icbms.so.device.logic;

import com.pineone.icbms.so.device.entity.*;
import com.pineone.icbms.so.device.proxy.DeviceControlProxy;
import com.pineone.icbms.so.device.proxy.DeviceICollectionProxy;
import com.pineone.icbms.so.device.store.DeviceResultStore;
import com.pineone.icbms.so.device.store.DeviceStore;
import com.pineone.icbms.so.device.util.ClientProfile;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DeviceManagerLogic implements DeviceManager {

    public static final Logger logger = LoggerFactory.getLogger(DeviceManagerLogic.class);

    @Autowired
    private DeviceStore deviceStore;

    @Autowired
    private DeviceResultStore deviceResultStore;

    @Autowired
    private DeviceICollectionProxy deviceICollectionProxy;

    @Autowired
    private DeviceControlProxy deviceControlProxy;

    @Override
    public void deviceRegister(deviceReleaseMessage deviceReleaseMessage){
        // NOTE : 디바이스 생성.
        logger.debug("DeviceReleaseMessage = " + deviceReleaseMessage.toString());
        // 디바이스 ID로 SDA에 데이터 요청.
        Device device = deviceRequest(ClientProfile.SDA_DATAREQUEST_URI + ClientProfile.SDA_DEVICE + deviceReleaseMessage.getDeviceId());
        logger.debug("Device = " + device.toString());
        // 디바이스 저장.
        deviceCreate(device);
    }

    @Override
    public void deviceRelease(String deviceId){
        logger.debug("Device ID = " + deviceId);
        deviceStore.delete(deviceId);
    }

    @Override
    public String deviceExecute(String deviceId,String deviceCommand){

        logger.debug("DeviceID = " + deviceId + " DeviceCommand = " + deviceCommand);

        String commandId = ClientProfile.SI_COMMAND_ID + System.nanoTime();

        // DB에서 Device를 얻음.
        // Device device = makeDeviceData(deviceId, deviceService, deviceCommand);

        // SI를 제어할수 있는 DeviceControlMessage로 변환
        DeviceControlMessage deviceControlMessage = deviceDataConversion(deviceId,commandId,deviceCommand);
        logger.debug("DeviceControlMessage = " + deviceControlMessage.toString());
        // JsonData로 요청 메시지 변환
        String jsonString = convertObjectToJson(deviceControlMessage);
        logger.debug("DeviceControlMessage to JSON = " + jsonString);
        // Device 제어 요청 보냄.
//        ResultMessage resultMessage = controlRequest(ClientProfile.SI_CONTOL_URI,jsonString);
//
        // Device 제어 결과 저장.
//        controlResultsStorage(deviceId, commandId, deviceCommand, resultMessage);

//        return resultMessage.get_result();
        return "Test Success";
    }

    @Override
    public String deviceControlResult(ResultMessage resultMessage) {
        logger.debug("ResultMessage = " + resultMessage.toString());
        DeviceResult deviceResult = deviceResultStore.retrieve(resultMessage.get_commandId());

        if(deviceResult != null && deviceResult.getCommandId() != null){
            // It has been confirmed for the linked data.
            if(ClientProfile.RESPONSE_SUCCESS_CODE.equals(resultMessage.get_resultCode()) ||
                    ClientProfile.RESPONSE_SUCCESS.equals(resultMessage.get_resultCode()) ||
                    ClientProfile.RESPONSE_SUCCESS_ONEM2MCODE.equals(resultMessage.get_resultCode())){

                deviceResult.setResult2(resultMessage.get_resultCode());
                deviceResultStore.update(deviceResult);
            }
            return resultMessage.get_resultCode();
        } else {
            return "No device Control Message.";
        }
    }

    @Override
    public Device deviceSearchById(String deviceId) {
        logger.debug("Device ID = " + deviceId);
        return deviceStore.retrieveByID(deviceId);
    }

    @Override
    public List<Device> deviceSearchByLocation(String location) {
        logger.debug("Location = " + location);
        return deviceStore.retrieveByLocation(location);
    }

    @Override
    public List<String> requestDeviceServiceList(String location) {
        logger.debug("Location = " + location);
        return deviceStore.retrieveDeviceService(location);
    }

    @Override
    public String searchOperation(String deviceId, String deviceService) {
        //SDA에 DeviceId와 deviceService를 보낸다.
        logger.debug("Device ID = " + deviceId + " DeviceService = " + deviceService);
        String responseData = deviceICollectionProxy.findDeviceOperation(deviceId,deviceService);
        return responseData;
    }

    @Override
    public List<Device> searchDeviceList() {
        List<Device> deviceList = deviceStore.retrieveDeviceList();
        for(Device device : deviceList){
            logger.debug("Device = " + device.toString());
        }
        return deviceList;
    }


    private DeviceControlMessage deviceDataConversion(String deviceId, String commandId, String deviceCommand){
        DeviceControlMessage deviceControlMessage = new DeviceControlMessage();

        deviceControlMessage.set_uri(deviceId);
        deviceControlMessage.set_notificationUri(ClientProfile.SO_CONTROL_NOTIFICATON_URI);
        deviceControlMessage.set_commandId(commandId);
        deviceControlMessage.set_command(ClientProfile.SI_CONTROL_ACTION);
        deviceControlMessage.setCnf(ClientProfile.SO_CONTROL_TYPE);
        deviceControlMessage.setCon(deviceCommand);

        return deviceControlMessage;
    }

    private String convertObjectToJson(DeviceControlMessage deviceControlMessage){
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(deviceControlMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    private ResultMessage controlRequest(String uri,String body){
        //
        ResultMessage resultMessage= deviceControlProxy.deviceControlRequest(uri,body);
        return resultMessage;
    }

    private Device deviceRequest(String uri){
        //
        Device device = deviceICollectionProxy.findDeviceByID(uri);
        return device;
    }

    private void controlResultsStorage(String deviceId, String commandId, String deviceCommand, ResultMessage resultMessage){

        DeviceResult deviceResult = new DeviceResult();

        deviceResult.setCommandId(commandId);
        deviceResult.setSendMessage(deviceId + deviceCommand);
        deviceResult.setDeviceUrl(deviceId);
        deviceResult.setValue(deviceCommand);
        deviceResult.setResult1(resultMessage.get_result());
        deviceResult.setResult2("");

        deviceResultStore.create(deviceResult);

    }

    private void deviceCreate(Device device){
        deviceStore.create(device);
    }

}