
package dev.greyferret.ferretbot.entity.json.v5.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Links {

    @SerializedName("self")
    @Expose
    private String self;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Links() {
    }

    /**
     * 
     * @param self
     */
    public Links(String self) {
        super();
        this.self = self;
    }
}
