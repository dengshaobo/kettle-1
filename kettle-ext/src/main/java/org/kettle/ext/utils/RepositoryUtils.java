package org.kettle.ext.utils;

import org.kettle.ext.App;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.TransMeta;

/**
 * @description: 资源库工具
 * @author: ZX
 * @date: 2018/11/21 11:36
 */
public class RepositoryUtils {

    public static JobMeta loadJobByPath(String path) throws KettleException {
        String dir = path.substring(0, path.lastIndexOf("/"));
        String name = path.substring(path.lastIndexOf("/") + 1);

        Repository repository = App.getInstance().getRepository();
        try {
            RepositoryDirectoryInterface directory = repository.findDirectory(dir);
            if (directory == null) {
                directory = repository.getUserHomeDirectory();
            }
            return App.getInstance().getRepository().loadJob(name, directory, null, null);
        } catch (KettleException e) {
            //数据库连接出现问题后kettle内部api资源库连接失效需要捕获异常后重新连接
            e.printStackTrace();
            if (e instanceof KettleException) {
                App.getInstance().reConnect();
            }
            return null;
        }
    }

    public static TransMeta loadTransByPath(String path) throws KettleException {
        String dir = path.substring(0, path.lastIndexOf("/"));
        String name = path.substring(path.lastIndexOf("/") + 1);
        Repository repository = App.getInstance().getRepository();
        try {
            RepositoryDirectoryInterface directory = repository.findDirectory(dir);
            if (directory == null) {
                directory = repository.getUserHomeDirectory();
            }
            return App.getInstance().getRepository().loadTransformation(name, directory, null, true, null);
        } catch (KettleException e) {
            //数据库连接出现问题后kettle内部api资源库连接失效需要捕获异常后重新连接
            e.printStackTrace();
            if (e instanceof KettleException) {
                App.getInstance().reConnect();
            }
            return null;
        }
    }

    public static RepositoryDirectoryInterface loadDirectory(String path) throws KettleException {
        String dir = path.substring(0, path.lastIndexOf("/"));
        String name = path.substring(path.lastIndexOf("/") + 1);

        Repository repository = App.getInstance().getRepository();
        RepositoryDirectoryInterface directory = repository.findDirectory(dir);
        if (directory == null) {
            directory = repository.getUserHomeDirectory();
        }

        return directory;
    }

}
