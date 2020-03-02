package io.horizontalsystems.lightningkit.demo.restorewallet

import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.App
import io.horizontalsystems.lightningkit.demo.core.Storage
import io.reactivex.disposables.CompositeDisposable

class RestoreWalletInteractor(val filesDir: String, val storage: Storage) :
    RestoreWalletModule.IInteractor {
    lateinit var delegate: RestoreWalletPresenter
    private val disposables = CompositeDisposable()

    val lightningKit by lazy { LightningKit.local(filesDir) }

    override fun restoreWallet(mnemonicList: List<String>) {
        val password = "superstrongpw"

        lightningKit.initWallet(mnemonicList, password)
            .subscribe({
                storage.saveLocalLndPassword(password)

                App.lightningKit = lightningKit

                delegate.onRestoreWallet()
            }, {
                delegate.onError(it)
            })
            .let {
                disposables.add(it)
            }
    }
}
