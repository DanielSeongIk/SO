package com.pineone.icbms.so.device.pr;

import com.pineone.icbms.so.device.entity.Device;
import com.pineone.icbms.so.device.entity.DeviceTransFormObject;
import com.pineone.icbms.so.device.entity.ResultMessage;
import com.pineone.icbms.so.device.entity.deviceReleaseMessage;
import com.pineone.icbms.so.device.logic.DeviceManager;
import com.pineone.icbms.so.util.logprint.LogPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value ="/device")
public class DevicePresentation {

    /**
     * Device 제어 요청
     * Device 등록 요청
     * Device 해제 요청
     * Device 제어 결과 요청
     *
     * Device 검색 요청(By ID)
     * Device 검색 요청(By Location)
     * Device Service들 요청.
     * Device Operation 요청
     */

    public static final Logger logger = LoggerFactory.getLogger(DevicePresentation.class);

    @Autowired
    private DeviceManager deviceManager;

    /**
     *  Device 제어 요청
     */
    @RequestMapping(value = "/control",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public String deviceControl(@RequestBody DeviceTransFormObject deviceTransFormObject){
        // NOTE : Device Control
        logger.info(LogPrint.inputInfoLogPrint());
        System.out.println("\n**********  Device Presentation RequestDeviceControl  **********");
        System.out.println("Response DeviceId = " + deviceTransFormObject.getDeviceId());
        System.out.println("Response DeviceCommand = " + deviceTransFormObject.getDeviceCommand());
        return deviceManager.deviceExecute(deviceTransFormObject.getDeviceId(), deviceTransFormObject.getDeviceCommand());
    }

    /**
     *  Device List 검색.
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<Device> findDeviceList(){
        // Search Device List
        logger.info(LogPrint.inputInfoLogPrint());
        return deviceManager.searchDeviceList();
    }

    /**
     *  Device 검색. By ID
     */
    @RequestMapping(value = "/{deviceId}",method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public Device findDeviceById(@PathVariable String deviceId){
        // Search Device By Id
        logger.info(LogPrint.inputInfoLogPrint());
        return deviceManager.deviceSearchById(deviceId);
    }

    /**
     *  Device 검색. By Location
     */
    @RequestMapping(value = "/location/{place}",method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<Device> findDeviceByLocation(@PathVariable String place){
        // Search Device By Location
        logger.info(LogPrint.inputInfoLogPrint());
        return deviceManager.deviceSearchByLocation(place);
    }

    /**
     *  Device 서비스 리스트 검색
     */
    @RequestMapping(value = "/service/{location}",method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<String> deviceServiceList(@PathVariable String location) {
        logger.info(LogPrint.inputInfoLogPrint());
        return deviceManager.requestDeviceServiceList(location);
    }


    /**
     *  Device 등록 노티 SI -> SO
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void deviceEnableNotification(@RequestBody deviceReleaseMessage message) {
        // NOTE : Register the device memory
        logger.info(LogPrint.inputInfoLogPrint());
        deviceManager.deviceRegister(message);
    }


    /**
     *  Device 해제 노티 SI -> SO
     */
    @RequestMapping(value ="",method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deviceDisableNotification(@RequestBody deviceReleaseMessage message) {
        // NOTE : Unregister the device memory
        logger.info(LogPrint.inputInfoLogPrint());
        deviceManager.deviceRelease(message.getDeviceId());
    }


    /**
     *  Device 제어 결과 노티 SI -> SO
     */
    @RequestMapping(value ="/monitor",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public String asynchronousControlResult(@RequestBody ResultMessage message){
        // NOTO : Device the result is stored in the data memory.
        logger.info(LogPrint.inputInfoLogPrint());
        return deviceManager.deviceControlResult(message);
    }

    /**
     *  Device Operation을 검색
     */
    @RequestMapping(value ="/operation",method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public String findDeviceOperation(@RequestBody DeviceTransFormObject deviceTransFormObject){
        logger.info(LogPrint.inputInfoLogPrint());
        return deviceManager.searchOperation(deviceTransFormObject.getDeviceId(), deviceTransFormObject.getDeviceServices());
    }

    public DeviceTransFormObject settingDeviceRequestData(String deviceid, String command){
        DeviceTransFormObject object = new DeviceTransFormObject();
        object.setDeviceId(deviceid);
        object.setDeviceCommand(command);
        return object;
    }
}
