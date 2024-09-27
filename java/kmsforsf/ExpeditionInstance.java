// Erik Icket, ON4PB - 2024
package kmsforsf;

import com.fasterxml.jackson.annotation.JsonFilter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@JsonFilter("userFilter")
public class ExpeditionInstance
{

    private SimpleIntegerProperty index = new SimpleIntegerProperty(0);
    private SimpleStringProperty callsign = new SimpleStringProperty("");
    private SimpleStringProperty expedition = new SimpleStringProperty("");
    private SimpleStringProperty startDate = new SimpleStringProperty("");
    private SimpleStringProperty endDate = new SimpleStringProperty("");
    private SimpleStringProperty privateKey = new SimpleStringProperty("");
    private SimpleStringProperty publicKey = new SimpleStringProperty("");
    private SimpleStringProperty password = new SimpleStringProperty("");
    private SimpleBooleanProperty select = new SimpleBooleanProperty(false);

    public ExpeditionInstance()
    {
    }

    public ExpeditionInstance(int index, String callsign, String expedition, String startDate, String endDate, String privateKey, String publicKey, String password, boolean select)
    {
        this.index = new SimpleIntegerProperty(index);
        this.callsign = new SimpleStringProperty(callsign);
        this.expedition = new SimpleStringProperty(expedition);
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
        this.privateKey = new SimpleStringProperty(privateKey);
        this.publicKey = new SimpleStringProperty(publicKey);
        this.password = new SimpleStringProperty(password);
        this.select = new SimpleBooleanProperty(select);
    }

    public int getIndex()
    {
        return index.get();
    }

    public void setIndex(int iIndex)
    {
        index.set(iIndex);
    }

    public String getCallsign()
    {
        return callsign.get();
    }

    public void setCallsign(String sCallsign)
    {
        callsign.set(sCallsign);
    }

    public String getExpedition()
    {
        return expedition.get();
    }

    public void setExpedition(String sExpedition)
    {
        expedition.set(sExpedition);
    }

    public String getStartDate()
    {
        return startDate.get();
    }

    public void setStartDate(String sStartDate)
    {
        startDate.set(sStartDate);
    }

    public String getEndDate()
    {
        return endDate.get();
    }

    public void setEndDate(String sEndDate)
    {
        endDate.set(sEndDate);
    }

    public String getPrivateKey()
    {
        return privateKey.get();
    }

    public void setPrivateKey(String sPrivateKey)
    {
        privateKey.set(sPrivateKey);
    }

    public String getPublicKey()
    {
        return publicKey.get();
    }

    public void setPublicKey(String sPublicKey)
    {
        publicKey.set(sPublicKey);
    }

    public String getPassword()
    {
        return password.get();
    }

    public void setPassword(String sPassword)
    {
        password.set(sPassword);
    }

    public boolean getSelect()
    {
        return select.get();
    }

    public void setSelect(boolean bSelect)
    {
        select.set(bSelect);
    }

    public BooleanProperty selectProperty()
    {
        return select;
    }

}
