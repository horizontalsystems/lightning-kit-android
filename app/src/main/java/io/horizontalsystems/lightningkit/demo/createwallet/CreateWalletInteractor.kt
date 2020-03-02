package io.horizontalsystems.lightningkit.demo.createwallet

import io.horizontalsystems.lightningkit.LightningKit
import io.horizontalsystems.lightningkit.demo.core.App
import io.horizontalsystems.lightningkit.demo.core.Storage
import io.reactivex.disposables.CompositeDisposable

class CreateWalletInteractor(private val filesDir: String, private val storage: Storage) : CreateWalletModule.IInteractor {
    lateinit var delegate: CreateWalletModule.IInteractorDelegate
    private val disposables = CompositeDisposable()

    val lightningKit by lazy { LightningKit.local(filesDir) }

    override fun generateSeed() {
        lightningKit.genSeed()
            .subscribe({
                delegate.onSeedGenerated(it)
            }, {
                delegate.onSeedGenerateError(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun initWallet(mnemonicList: List<String>) {
        val password = "somesuperstrongpw"

        lightningKit.initWallet(mnemonicList, password)
            .subscribe({
                storage.saveLocalLndPassword(password)

                App.lightningKit = lightningKit

                delegate.onWalletInit()
            }, {
                delegate.onWalletInitError(it)
            })
            .let {
                disposables.add(it)
            }
    }

    override fun clear() {
        disposables.clear()
    }
}
