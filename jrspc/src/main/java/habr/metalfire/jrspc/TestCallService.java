package habr.metalfire.jrspc;

import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class TestCallService extends AbstractService{

    private static final long serialVersionUID = 1L;
    
    
    
    @Remote
    public String testPublicMethod(){           
        return "testPublicMethod ok";        
    }   
    
    @Secured("User")
    @Remote
    public String testUserMethod(){           
        return "testUserMethod ok, because you are loged in.";        
    }     
     
    @Secured("Admin")
    @Remote
    public String testAdminMethod(){           
        return "testAdminMethod ok, because you are is admin.";        
    }     
              
    
    @Remote
    public String testArrayArguments(Long userId, String role, boolean test,
            List<User> users, User user){    
         return "passed arguments: "+ new JSONObject()
        .accumulate("userId", userId).accumulate("role", role)
        .accumulate("test", test).accumulate("users", users)
        .accumulate("user", user);     
    }   
    
    @Remote
    public String testObjectArgument(User user){    
         return "passed argument: "+  new JSONObject().accumulate("user", user);      
    }       
    
    @Remote
    public String testJSONObjectArgument(JSONObject user){    
         return "passed argument: "+  user;      
    }      
    
    @Remote
    public String testPrimitiveArgument(int  numberParam){    
          return "numberParam="+numberParam;        
    }       
    
    @Remote
    public String testPrimitivesListArgument(List<Integer>  numbers){    
          return "passed PrimitivesList: "+ new JSONObject().accumulate("numbers", numbers);        
    }    
    
    @Remote
    public String testPrimitivesArrayArgument(Integer[]  numbers){    
          return "passed PrimitivesArray: "+ new JSONObject().accumulate("numbers", numbers);        
    }        
    
    @Remote
    public String testObjectsListArgument(List<User>  users){    
          return "passed ObjectsList: "+ new JSONObject().accumulate("users", users);        
    } 
    
    @Remote
    public String testObjectsArrayArgument(User[]  users){    
          return "passed ObjectsArray: "+ new JSONObject().accumulate("users", users);        
    }   
    
    @Remote
    public String testObjectsArguments(User  user1, User  user2){    
          return "passed ObjectsArguments: "+ 
            new JSONObject().accumulate("user1", user1).accumulate("user2", user2);        
    }            
}
