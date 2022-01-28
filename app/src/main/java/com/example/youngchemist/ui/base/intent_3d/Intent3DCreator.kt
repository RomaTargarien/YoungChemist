package com.example.youngchemist.ui.base.intent_3d

import android.content.Intent
import android.net.Uri

class Intent3DCreator {

    companion object {
        private const val SCENE_VIEWER_URI = "https://arvr.google.com/scene-viewer/1.0"
        private const val FILE = "file"
        private const val MODE = "mode"
        private const val MODE_TYPE = "3d_only"
        private const val PACKAGE_NAME = "com.google.ar.core"

        fun create3DIntent(uri: String): Intent {
            val intentUri = Uri.parse(SCENE_VIEWER_URI)
                .buildUpon()
                .appendQueryParameter(FILE, uri)
                .appendQueryParameter(MODE, MODE_TYPE)
                .build()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setData(intentUri)
            intent.setPackage(PACKAGE_NAME)
            return intent
        }
    }
}