package habr.metalfire.jrspc;

import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class TestAdminService extends AbstractService{

    private static final long serialVersionUID = 1L;
    
    @Autowired
    UserManager userManager;    
               
    private User checkUser(Long userId){
        User user = userManager.findById(userId);
        if(user == null){throw new RuntimeException("User with id "+userId+" not found!");}
        return user;        
    }
    

   
    
    @Secured("Admin")   
    @Remote
    public String grantRole(Long userId, String role){    
        User clientUser = userManager.findById(userId);   
        if(clientUser == null){throw new RuntimeException("User with id: "+userId+" not found!");}
        User serverUser = getUser();        
        if(clientUser.getId().equals(serverUser.getId())){throw new RuntimeException("You cannot change own role!");}
        clientUser.setRole(role); 
        userManager.updateUser(clientUser);
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
    public Integer getUsersCount(){        
        return userManager.getUsersCount();
    }        
}
