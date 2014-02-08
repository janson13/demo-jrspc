package habr.metalfire.jrspc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommonServiceController {

    final static Log log = LogFactory.getLog(CommonServiceController.class);

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private HttpSession session;
    
    @RequestMapping(value = "/ajax-request", method = RequestMethod.POST)
    @ResponseBody
    private String processAjaxRequest(@RequestBody String requestJson) {
        //log.debug("requestJson="+requestJson);    
        JSONObject request = JSONObject.fromObject(requestJson);
        String serviceName = request.optString("service");
        String methodName = request.optString("method");
        JSONObject params = request.optJSONObject("params");  
        log.debug("request ="+request);
        JSONObject response = callServiceMethod(serviceName, methodName, params);
        log.debug("response="+response);
        return response.toString();       
    }    

    private JSONObject callServiceMethod(String serviceName, String methodName, JSONObject params) {
        JSONObject response =  new JSONObject(); 
        try {
            Object serviceObject = applicationContext.getBean(serviceName);
            if (serviceObject == null) {
                throw new RuntimeException("AbstractService bean with name " + serviceName + " not found!");
            }
            if (!(serviceObject instanceof AbstractService)) {
                throw new RuntimeException("Collable service \""+serviceName+"\" MUST be instance of AbstractService, but not of: "
                        + serviceObject.getClass().getName());
            }
            AbstractService service = (AbstractService) serviceObject;             
            User user = (User) session.getAttribute("user");
            service.setUser(user);
            Object result = invokeMethod(service, methodName, params);          
            if(result != null){
                response.put("result", result);
            } else{
                response.put("result", new JSONObject());
            }                            
        } catch (Throwable th) {       
            response.put("error", th.getMessage());       
        }
        return response;
    }  

    private Object invokeMethod(AbstractService service, String methodName, JSONObject methodParams) throws Throwable {
        try {                                
            User user = service.getUser();
            log.debug("user="+ JSONObject.fromObject(user));            
            Class<?> ownerClass = service.getClass();
            Class<?>[] parameterTypes = new Class[] { JSONObject.class };
            Object[] arguments = new Object[] { methodParams };
            Method actionMethod = ownerClass.getMethod(methodName, parameterTypes);
            checkAccess(actionMethod, methodParams, user);                   
            Object result = actionMethod.invoke(service, arguments);          
            return result == null ? new Object() : result;            
        } catch (Throwable th) {
            if (th instanceof InvocationTargetException) {
                th = ((InvocationTargetException) th).getTargetException();
            } 
            if (th instanceof NoSuchMethodException) {
                th = new RuntimeException("Method \""+methodName+"\" not found on class \""+service.getClass().getName()+"\"!");
            }         
            throw th;
        }
    }

    private void checkAccess(Method method, Object methodParams, User user) {
        if (!method.isAnnotationPresent(Remote.class)) {
            throw new RuntimeException("Remotely invoked method MUST be annotated as Remote!");
        }                
        if (method.isAnnotationPresent(Secured.class)) {
            String[] roles = method.getAnnotation(Secured.class).value();            
            if ( user == null || ( !Arrays.asList(roles).contains(user.getRole()) && !"Admin".equals(user.getRole()) ) ) {
                String message = "User not in role: "
                            + StringUtils.arrayToDelimitedString(roles, " or ")                     
                            + ", required for invocation of \""
                            + method.getName() + "\" method !";               
                throw new RuntimeException(message);
            }
        }         
    }      
}