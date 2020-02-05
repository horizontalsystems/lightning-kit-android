package io.horizontalsystems.lightningkit.demo.restorewallet

import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.Storage
import io.reactivex.disposables.CompositeDisposable

class RestoreWalletInteractor(val filesDir: String, val storage: Storage) :
    RestoreWalletModule.IInteractor {
    lateinit var delegate: RestoreWalletPresenter
    private val disposables = CompositeDisposable()

    override fun restoreWallet(mnemonicList: List<String>) {
        val password = "superstrongpw"

        LightningKit.restoreLocal(filesDir, password, mnemonicList)
            .doOnSuccess {
                storage.saveLocalLndPassword(password)
            }
            .subscribe({
                delegate.onRestoreWallet()
            }, {
                delegate.onError(it)
            })
            .let {
                disposables.add(it)
            }
    }
}
