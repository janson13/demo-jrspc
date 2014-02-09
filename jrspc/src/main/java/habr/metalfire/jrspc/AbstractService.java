package habr.metalfire.jrspc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** parent class for all services */

public abstract class AbstractService {

    protected  Log log = LogFactory.getLog(this.getClass());
    
    private User user;
                
    public void setUser(User user) {          
         this.user = user;
    } 
     
    public User getUser() {          
        return user;
    }  
    
}
