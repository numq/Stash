package com.numq.stash.transfer

import com.numq.stash.interactor.UseCase
import kotlinx.coroutines.channels.Channel

class GetTransferActions constructor(
    private val service: TransferService
) : UseCase<Unit, Channel<TransferAction>>() {
    override suspend fun execute(arg: Unit) = service.actions
}