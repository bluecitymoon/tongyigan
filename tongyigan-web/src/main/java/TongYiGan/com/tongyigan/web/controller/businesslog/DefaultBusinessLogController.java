package TongYiGan.com.tongyigan.web.controller.businesslog;

import java.util.HashMap;
import java.util.Map;

import org.dayatang.domain.InstanceFactory;
import org.dayatang.querychannel.Page;
import org.openkoala.businesslog.application.BusinessLogApplication;
import org.openkoala.businesslog.model.DefaultBusinessLogDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/log")
public class DefaultBusinessLogController {


    private BusinessLogApplication businessLogApplication;


    @ResponseBody
    @RequestMapping("/list")
    public Page pageJson(DefaultBusinessLogDTO defaultBusinessLogDTO,
                                        @RequestParam int page, @RequestParam int pagesize) {
        Page<DefaultBusinessLogDTO> result = getBusinessLogApplication().pageQueryDefaultBusinessLog(defaultBusinessLogDTO, page, pagesize);
        return result;
    }


    @ResponseBody
    @RequestMapping("/delete")
    public Map<String, Object> delete(@RequestParam String ids) {
        Map<String, Object> result = new HashMap<String, Object>();
        String[] value = ids.split(",");
        Long[] idArrs = new Long[value.length];
        for (int i = 0; i < value.length; i++) {

            idArrs[i] = Long.parseLong(value[i]);

        }
        getBusinessLogApplication().removeDefaultBusinessLogs(idArrs);
        result.put("result", "success");
        return result;
    }

    @ResponseBody
    @RequestMapping("/get/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", getBusinessLogApplication().getDefaultBusinessLog(id));
        return result;
    }


    public BusinessLogApplication getBusinessLogApplication() {
        if (null == businessLogApplication) {
            businessLogApplication = InstanceFactory.getInstance(BusinessLogApplication.class);
        }
        return businessLogApplication;
    }
}