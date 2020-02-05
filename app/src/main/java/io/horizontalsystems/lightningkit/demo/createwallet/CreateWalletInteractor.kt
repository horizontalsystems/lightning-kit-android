package io.horizontalsystems.lightningkit.demo.createwallet

import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.Storage
import io.reactivex.disposables.CompositeDisposable

class CreateWalletInteractor(private val filesDir: String, private val storage: Storage) : CreateWalletModule.IInteractor {
    lateinit var delegate: CreateWalletModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun createWallet() {
        val password = "somesuperstrongpw"

        LightningKit.createLocal(filesDir, password)
            .doOnSuccess {
                storage.saveLocalLndPassword(password)
            }
            .subscribe({
                delegate.onCreateWallet(it)
            }, {
                delegate.onSeedGenerateError(it)
            })
            .let {
                disposables.add(it)
            }
    }
}
