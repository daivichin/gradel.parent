package com.gradel.parent.ali.ahas.dubbo;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.Directory;

/**
 * {@link FlowFailoverClusterInvoker}
 *
 */
public class FlowFailoverCluster implements Cluster {

    public final static String NAME = "flowfailover";

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new FlowFailoverClusterInvoker<T>(directory);
    }

}
