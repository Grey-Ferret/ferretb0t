
package net.greyferret.ferretbot.entity.json.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Uploads {

    @SerializedName("containers")
    @Expose
    private Containers containers;
    @SerializedName("processor")
    @Expose
    private Processor processor;
    @SerializedName("storage")
    @Expose
    private Storage storage;

    public Containers getContainers() {
        return containers;
    }

    public void setContainers(Containers containers) {
        this.containers = containers;
    }

    public Processor getProcessor() {
        return processor;
    }

    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

}
