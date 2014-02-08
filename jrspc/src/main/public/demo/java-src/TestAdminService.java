package habr.metalfire.jrspc;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class TestAdminService extends AbstractService{

    @Autowired
    UserManager userManager;    
               
    private User checkUser(Long userId){
        User user = userManager.findById(userId);
        if(user == null){throw new RuntimeException("User with id "+userId+" not found!");}
        return user;        
    }
   
    @Secured("Admin")   
    @Remote
    public String grantRole(JSONObject params){    
        Long userId = params.optLong("userId");  
        User user = userManager.findById(userId);
        String role = params.optString("role");             
        if(user.getId().equals(getUser().getId())){throw new RuntimeException("Admin role cannot be revoked!");}
        user.setRole(role); 
        userManager.updateUser(user);
        return "role "+role+" granted to user "+userId;        
    } 
    
    
    @Secured("Admin")   
    @Remote
    public String removeUser(JSONObject params){ 
        User user = checkUser(params.optLong("userId"));
        if("Admin".equals(user.getRole())){throw new RuntimeException("Admin cannot be removed!");}
        userManager.deleteUser(user);
        return "User "+user.getId()+" removed.";        
    }     
    
    @Remote
    public Integer getUsersCount(JSONObject params){        
        return userManager.getUsersCount();
    }        
}
