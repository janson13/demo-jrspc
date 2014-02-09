package habr.metalfire.jrspc;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class TestUserService extends AbstractService{

    @Autowired
    UserManager userManager;    
    
    @Autowired
    private HttpSession session;
               
    @Remote
    public Long registerUser(JSONObject userJson){        
        User user = (User) JSONObject.toBean(userJson, User.class);        
        if(userManager.findByLogin(user.getLogin()) != null){
          throw new RuntimeException("User with login "+user.getLogin()+" already registered!");
        }           
        if(userManager.getUsersCount() == 0){
          user.setRole(User.Role.Admin.name());
        }else{
          user.setRole(User.Role.User.name());
        } 
        userManager.saveUser(user); 
        return user.getId();
    }    
    
    @Remote
    public User logIn(JSONObject params){      
         String error = "Unknown combination of login and password!";
         User user = userManager.findByLogin(params.optString("login"));
         if(user == null){ throw new RuntimeException(error);}
         if(!user.getPassword().equals(params.optString("password"))){ throw new RuntimeException(error);} 
         session.setAttribute("user", user);
         return user;
    }     
    
    @Secured("User") 
    @Remote
    public void logOut(JSONObject params){       
         session.removeAttribute("user");
    }           
    
    @Secured("User")   
    @Remote
    public void changeCity(JSONObject params){   
        String city = params.optString("city");                
        User user = getUser();
        user.setCity(city);                
        userManager.updateUser(user);
    }           
 
    @Remote
    public User getSessionUser(JSONObject params){           
        try{
           return (User) session.getAttribute("user");
        }catch(Throwable th){log.debug("in checkUser: "+th);}
        return null;
    }    
    
}
