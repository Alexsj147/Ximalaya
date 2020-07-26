package alex.example.ximalaya.base;

public interface IBasePresenter<T> {
    /**
     * 注册UI的回调接口
     * @param t
     */
    void registerViewCallBack(T t);

    /**
     * 取消注册
     * @param t
     */
    void unRegisterViewCallBack(T t);
}
