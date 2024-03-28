package com.jxdinfo.doc.manager.docmanager.ex;

/**
 * @author dushitaoyuan
 *  业务异常
 */
public class ServiceException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -907834946583023231L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

}
