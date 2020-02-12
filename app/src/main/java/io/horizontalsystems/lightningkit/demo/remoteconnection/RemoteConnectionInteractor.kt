package io.horizontalsystems.lightningkit.demo.remoteconnection

import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.Storage
import io.horizontalsystems.lightningkit.remote.RemoteLndCredentials
import io.reactivex.disposables.CompositeDisposable

class RemoteConnectionInteractor(private val storage: Storage) : RemoteConnectionModule.IInteractor {
    lateinit var delegate: RemoteConnectionModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun validateConnection(remoteLndCredentials: RemoteLndCredentials) {
        LightningKit
            .validateRemoteConnection(remoteLndCredentials)
            .subscribe({
                delegate.onValidationSuccess(remoteLndCredentials)
            }, {
                delegate.onValidationFailed(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun saveRemoteLndCredentials(remoteLndCredentials: RemoteLndCredentials) {
        storage.saveRemoteLndCredentials(remoteLndCredentials)
    }

    override fun clear() {
        disposables.clear()
    }
}