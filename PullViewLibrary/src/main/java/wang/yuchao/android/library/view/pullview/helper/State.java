/**
 *  Copyright (c)  1993-2023 AutoNavi, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of AutoNavi, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with AutoNavi.
 */
package wang.yuchao.android.library.view.pullview.helper;

/**
 * 上拉下拉控件PullView的各个状态
 */
public enum State {

    /** PullView的默认值：NULL */
    NULL,

    /** PullView达到下拉刷新或者上拉加载更多的状态：初始化：INIT */
    INIT,

    /** PullView在拖动状态但是还没达到可以释放的状态：PULL */
    PULL,

    /** PullView达到可以释放的状态：RELEASE */
    RELEASE,

    /** PullView释放后的状态,包括正在刷新REFRESHING或者正在加载更多状态LOADING_MORE: LOADING */
    LOADING,

    /** 没有更多数据：NO_MORE_DATA */
    NO_MORE_DATA
}
