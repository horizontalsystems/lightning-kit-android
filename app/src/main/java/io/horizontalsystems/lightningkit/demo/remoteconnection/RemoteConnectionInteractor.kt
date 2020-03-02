package io.horizontalsystems.lightningkit.demo.remoteconnection

import io.horizontalsystems.lightningkit.ILndNode
import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.App
import io.horizontalsystems.lightningkit.demo.core.Storage
import io.horizontalsystems.lightningkit.remote.RemoteLndCredentials
import io.reactivex.disposables.CompositeDisposable

class RemoteConnectionInteractor(private val storage: Storage) : RemoteConnectionModule.IInteractor {
    lateinit var delegate: RemoteConnectionModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun validateConnection(remoteLndCredentials: RemoteLndCredentials) {
        val lightningKit = LightningKit.remote(remoteLndCredentials)
        val status = lightningKit.status

        if (status !is ILndNode.Status.ERROR) {
            storage.saveRemoteLndCredentials(remoteLndCredentials)
            App.lightningKit = lightningKit

            delegate.onValidationSuccess(remoteLndCredentials)
        } else {
            delegate.onValidationFailed(status.throwable)
        }
    }

    override fun clear() {
        disposables.clear()
    }
}