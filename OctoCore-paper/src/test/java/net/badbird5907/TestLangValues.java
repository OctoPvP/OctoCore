package net.badbird5907;

import net.octopvp.octocore.paper.utils.msg.Lang;
import org.junit.Assert;
import org.junit.Test;

public class TestLangValues {

    @Test
    public void testLangValues(){
        String lang = Lang.TEST.getMsg("a");
        Assert.assertEquals("a",lang);
    }
}
