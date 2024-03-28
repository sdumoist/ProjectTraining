//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jxdinfo.hussar.core.beetl;

import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.config.properties.HussarProperties;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.encrypt.AbstractCryptoProvider;
import com.jxdinfo.hussar.core.util.ToolUtil;
import com.jxdinfo.hussar.encrypt.util.SM2Util;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class BeetlConfiguration extends BeetlGroupUtilConfiguration {
    @Autowired
    private HussarProperties hussarProperties;
    @Autowired
    private HussarCacheManager hussarCacheManager;
    @Autowired
    private AbstractCryptoProvider crypto;
    @Autowired
    private GlobalProperties global;

    public BeetlConfiguration() {
    }

    public void initOther() {
        this.groupTemplate.registerFunctionPackage("shiro", new ShiroExt());
        this.groupTemplate.registerFunctionPackage("tool", new ToolUtil());
        this.groupTemplate.registerFunctionPackage("defaultValue", DefaultValue.class);
        this.groupTemplate.registerFunctionPackage("isRepeatAuthenticate", new IsRepeatAuthenticateTag());
        this.groupTemplate.registerTag("multiSelect", MultiSelectTag.class);
        this.groupTemplate.registerTag("select", SelectTag.class);
        this.groupTemplate.registerTag("radio", RadioTag.class);
        this.groupTemplate.registerTag("checkbox", CheckboxTag.class);
        String publicKey_backend = (String)this.hussarCacheManager.getObject("SM2BackEndPubKey", "SM2BackEndPubKey");
        String privateKey_backend = (String)this.hussarCacheManager.getObject("SM2BackEndPriKey", "SM2BackEndPriKey");
        String publicKey_fronend = (String)this.hussarCacheManager.getObject("SM2FronEndPubKey", "SM2FronEndPubKey");
        String privateKey_fronend = (String)this.hussarCacheManager.getObject("SM2FronEndPriKey", "SM2FronEndPriKey");
        if (publicKey_backend == null || privateKey_backend == null || publicKey_fronend == null || privateKey_fronend == null) {
            Map<String, String> keyMapBackEnd = SM2Util.generateKeyPair();
            Map<String, String> keyMapFronEnd = SM2Util.generateKeyPair();
            publicKey_backend = (String)keyMapFronEnd.get("pubKey");
            privateKey_backend = (String)keyMapBackEnd.get("priKey");
            publicKey_fronend = (String)keyMapBackEnd.get("pubKey");
            privateKey_fronend = (String)keyMapFronEnd.get("priKey");
            this.hussarCacheManager.setObject("SM2BackEndPubKey", "SM2BackEndPubKey", publicKey_backend);
            this.hussarCacheManager.setObject("SM2BackEndPriKey", "SM2BackEndPriKey", privateKey_backend);
            this.hussarCacheManager.setObject("SM2FronEndPubKey", "SM2FronEndPubKey", publicKey_fronend);
            this.hussarCacheManager.setObject("SM2FronEndPriKey", "SM2FronEndPriKey", privateKey_fronend);
        }

        String publicKey = (String)this.crypto.getKeyMap().get("publicKey");
        String securityType = this.crypto.getClass().getSimpleName().substring(0, this.crypto.getClass().getSimpleName().indexOf("$"));
        Map<String, Object> map = new HashMap();
        map.put("h_version", this.hussarProperties.getStaticVersion());
        map.put("spr", privateKey_fronend);
        map.put("puy", publicKey_fronend);
        map.put("securityType", securityType);
        map.put("publicKey", publicKey);
        map.put("signOpen", this.global.isEncryptSignOpen());
        map.put("kaptcha", this.hussarProperties.getKaptchaOpen());
        map.put("totp", this.hussarProperties.getTotpOpen());
        map.put("rememberme", this.hussarProperties.getRemebermeDays());
        map.put("hmUrl", this.hussarProperties.getHm());
        this.groupTemplate.setSharedVars(map);
    }
}
