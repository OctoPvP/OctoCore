package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashedAddress {
    private int hashedIp;
    
    public HashedAddress(String ip){
        this.hashedIp = ip.hashCode();
    }
    public boolean equals(String ip){
        return hashedIp == ip.hashCode();
    }
    public boolean equals(HashedAddress address){
        return hashedIp == address.getHashedIp();
    }
}
