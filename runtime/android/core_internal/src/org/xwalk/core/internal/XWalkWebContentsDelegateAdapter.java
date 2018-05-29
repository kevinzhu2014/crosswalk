// Copyright (c) 2013 Intel Corporation. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.xwalk.core.internal;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import org.chromium.content.browser.ContentVideoView;
import org.chromium.content_public.browser.InvalidateTypes;
import org.chromium.content_public.common.ContentUrlConstants;

class XWalkWebContentsDelegateAdapter extends XWalkWebContentsDelegate {
    private static final String TAG = XWalkWebContentsDelegateAdapter.class.getName();

    private XWalkContentsClient mXWalkContentsClient;
    private XWalkContent mXwalkContent;

    public XWalkWebContentsDelegateAdapter(XWalkContentsClient client, XWalkContent content) {
        mXWalkContentsClient = client;
        mXwalkContent = content;
    }

    @Override
    public boolean shouldCreateWebContents(String contentUrl) {
        if (mXWalkContentsClient != null) {
            return mXWalkContentsClient.shouldCreateWebContents(contentUrl);
        }
        return super.shouldCreateWebContents(contentUrl);
    }

    @Override
    public void loadingStateChanged(boolean toDifferentDocument) {
    	// TODO fix this; doesn't get called
        if (mXWalkContentsClient != null) {
            mXWalkContentsClient.onTitleChanged(mXwalkContent.getTitle(), false);
        }
    }
    
    @Override
    public void navigationStateChanged(int flags) {
    	
        if (mXWalkContentsClient != null) {
            // TODO (iotto): continue implementation if needed
            mXWalkContentsClient.onNavigationStateChanged(flags);
        }
        if ((flags & InvalidateTypes.URL) != 0 ) {
            
        }
//        if ((flags & InvalidateTypes.URL) != 0
//                && mAwContents.isPopupWindow()
//                && mXwalkContent.hasAccessedInitialDocument()) {
//            // Hint the client to show the last committed url, as it may be unsafe to show
//            // the pending entry.
//            String url = mXwalkContent.getLastCommittedUrl();
//            url = TextUtils.isEmpty(url) ? ContentUrlConstants.ABOUT_BLANK_DISPLAY_URL : url;
//            if (mXWalkContentsClient != null) {
//                mXWalkContentsClient.getCallbackHelper().postSynthesizedPageLoadingForUrlBarUpdate(url);
//            }
//        }
    }

    @Override
    public void onLoadProgressChanged(int progress) {
        if (mXWalkContentsClient != null) mXWalkContentsClient.onProgressChanged(progress);
    }

    @Override
    public boolean addNewContents(boolean isDialog, boolean isUserGesture) {
        return mXWalkContentsClient.onCreateWindow(isDialog, isUserGesture);
    }

    @Override
    public void closeContents() {
        if (mXWalkContentsClient != null) mXWalkContentsClient.onCloseWindow();
    }

    @Override
    public void activateContents() {
        if (mXWalkContentsClient != null) mXWalkContentsClient.onRequestFocus();
    }

    @Override
    public void rendererUnresponsive() {
        if (mXWalkContentsClient != null) mXWalkContentsClient.onRendererUnresponsive();
    }

    @Override
    public void rendererResponsive() {
        if (mXWalkContentsClient != null) mXWalkContentsClient.onRendererResponsive();
    }

    @Override
    public void handleKeyboardEvent(KeyEvent event) {
        // Handle the event here when necessary and return if so.
        if (mXWalkContentsClient != null) mXWalkContentsClient.onUnhandledKeyEvent(event);
    }

    @Override
    public boolean addMessageToConsole(int level, String message, int lineNumber,
            String sourceId) {
        if (mXWalkContentsClient == null) return false;
        ConsoleMessage.MessageLevel messageLevel = ConsoleMessage.MessageLevel.DEBUG;
        switch(level) {
            case LOG_LEVEL_TIP:
                messageLevel = ConsoleMessage.MessageLevel.TIP;
                break;
            case LOG_LEVEL_LOG:
                messageLevel = ConsoleMessage.MessageLevel.LOG;
                break;
            case LOG_LEVEL_WARNING:
                messageLevel = ConsoleMessage.MessageLevel.WARNING;
                break;
            case LOG_LEVEL_ERROR:
                messageLevel = ConsoleMessage.MessageLevel.ERROR;
                break;
            default:
                Log.w(TAG, "Unknown message level, defaulting to DEBUG");
                break;
        }
        return mXWalkContentsClient.onConsoleMessage(
                new ConsoleMessage(message, sourceId, lineNumber, messageLevel));
    }

    @Override
    public void showRepostFormWarningDialog() {
        if (mXWalkContentsClient == null) return;
        mXWalkContentsClient.resetSwipeRefreshHandler();
    }

    @Override
    public void toggleFullscreen(boolean enterFullscreen) {
        if (!enterFullscreen) {
            ContentVideoView videoView = ContentVideoView.getContentVideoView();
            if (videoView != null) videoView.exitFullscreen(false);
        }
        if (mXWalkContentsClient != null) mXWalkContentsClient.onToggleFullscreen(enterFullscreen);
    }

    @Override
    public boolean isFullscreen() {
        if (mXWalkContentsClient != null) return mXWalkContentsClient.hasEnteredFullscreen();

        return false;
    }

    @Override
    public boolean shouldOverrideRunFileChooser(int processId, int renderId, int mode,
            String acceptTypes, boolean capture) {
        if (mXWalkContentsClient != null) {
            return mXWalkContentsClient.shouldOverrideRunFileChooser(processId, renderId, mode,
                    acceptTypes, capture);
        }
        return false;
    }
}
