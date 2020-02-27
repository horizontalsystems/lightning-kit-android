package io.horizontalsystems.lightningkit.demo.createwallet

import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.App
import io.horizontalsystems.lightningkit.demo.core.Storage
import io.reactivex.disposables.CompositeDisposable

class CreateWalletInteractor(private val filesDir: String, private val storage: Storage) : CreateWalletModule.IInteractor {
    lateinit var delegate: CreateWalletModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    override fun createWallet() {
        val password = "somesuperstrongpw"

        val lightningKit = LightningKit.local(filesDir)

        lightningKit.create(password)
            .doOnSuccess {
                storage.saveLocalLndPassword(password)

                App.lightningKit = lightningKit
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

    override fun clear() {
        disposables.clear()
    }
}
