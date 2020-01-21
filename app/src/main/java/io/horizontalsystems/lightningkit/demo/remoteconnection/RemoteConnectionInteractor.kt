package io.horizontalsystems.lightningkit.demo.remoteconnection

import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.Storage
import io.reactivex.disposables.CompositeDisposable

class RemoteConnectionInteractor(private val storage: Storage) : RemoteConnectionModule.IInteractor {
    lateinit var delegate: RemoteConnectionModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun validateConnection(connectionParams: ConnectionParams) {
        LightningKit
            .validateRemoteConnection(connectionParams.host, connectionParams.port, connectionParams.certificate, connectionParams.macaroon)
            .subscribe({
                delegate.onValidationSuccess(connectionParams)
            }, {
                delegate.onValidationFailed(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun saveConnectionParams(connectionParams: ConnectionParams) {
        storage.saveConnectionParams(connectionParams)
    }

    override fun clear() {
        disposables.clear()
    }
}