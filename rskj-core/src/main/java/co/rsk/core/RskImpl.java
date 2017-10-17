/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.rsk.core;

import co.rsk.net.BlockStore;
import co.rsk.net.MessageHandler;
import co.rsk.net.NodeBlockProcessor;
import co.rsk.net.NodeMessageHandler;
import co.rsk.net.handler.TxHandlerImpl;
import co.rsk.scoring.PeerScoringManager;
import co.rsk.scoring.PunishmentParameters;
import org.ethereum.config.SystemProperties;
import org.ethereum.core.PendingState;
import org.ethereum.db.ReceiptStore;
import org.ethereum.facade.EthereumImpl;
import org.ethereum.listener.CompositeEthereumListener;
import org.ethereum.manager.AdminInfo;
import org.ethereum.manager.WorldManager;
import org.ethereum.net.server.ChannelManager;
import org.ethereum.net.server.PeerServer;
import org.ethereum.vm.program.invoke.ProgramInvokeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by ajlopez on 3/3/2016.
 */
@Component
public class RskImpl extends EthereumImpl implements Rsk {
    private boolean isplaying;
    private NodeBlockProcessor nodeBlockProcessor;
    private PeerScoringManager peerScoringManager;
    private MessageHandler messageHandler;
    private static final Object NMH_LOCK = new Object();
    private static final Object PSM_LOCK = new Object();

    @Autowired
    public RskImpl(WorldManager worldManager,
                   AdminInfo adminInfo,
                   ChannelManager channelManager,
                   PeerServer peerServer,
                   ProgramInvokeFactory programInvokeFactory,
                   PendingState pendingState,
                   SystemProperties config,
                   CompositeEthereumListener compositeEthereumListener,
                   ReceiptStore receiptStore,
                   ApplicationContext ctx) {
        super(worldManager, adminInfo, channelManager, peerServer, programInvokeFactory, pendingState, config, compositeEthereumListener, receiptStore, ctx);
    }

    @Override
    public PeerScoringManager getPeerScoringManager() {
        if (this.peerScoringManager == null) {
            synchronized (PSM_LOCK) {
                if (this.peerScoringManager == null) {
                    SystemProperties config = this.getSystemProperties();

                    int nnodes = config.scoringNumberOfNodes();

                    long nodePunishmentDuration = config.scoringNodesPunishmentDuration();
                    int nodePunishmentIncrement = config.scoringNodesPunishmentIncrement();
                    long nodePunhishmentMaximumDuration = config.scoringNodesPunishmentMaximumDuration();

                    long addressPunishmentDuration = config.scoringAddressesPunishmentDuration();
                    int addressPunishmentIncrement = config.scoringAddressesPunishmentIncrement();
                    long addressPunishmentMaximunDuration = config.scoringAddressesPunishmentMaximumDuration();

                    this.peerScoringManager = new PeerScoringManager(nnodes, new PunishmentParameters(nodePunishmentDuration, nodePunishmentIncrement, nodePunhishmentMaximumDuration), new PunishmentParameters(addressPunishmentDuration, addressPunishmentIncrement, addressPunishmentMaximunDuration));
                }
            }
        }

        return this.peerScoringManager;
    }

    @Override
    public MessageHandler getMessageHandler() {
        if (this.messageHandler == null) {
            synchronized (NMH_LOCK) {
                if (this.messageHandler == null) {
                    this.nodeBlockProcessor = getNodeBlockProcessor(); // Initialize nodeBlockProcessor if not done already.
                    NodeMessageHandler handler = new NodeMessageHandler(this.nodeBlockProcessor, getChannelManager(),
                            getWorldManager().getPendingState(), new TxHandlerImpl(getWorldManager()), this.getPeerScoringManager());
                    handler.start();
                    this.messageHandler = handler;
                }
            }
        }

        return this.messageHandler;
    }

    @Override
    public NodeBlockProcessor getNodeBlockProcessor() {
        if (this.nodeBlockProcessor == null) {
            this.nodeBlockProcessor = new NodeBlockProcessor(new BlockStore(), this.getWorldManager().getBlockchain(), this.getWorldManager());
        }
        return this.nodeBlockProcessor;
    }

    @Override
    public boolean isPlayingBlocks() {
        return isplaying;
    }

    @Override
    public boolean isSyncingBlocks() {
        return this.getNodeBlockProcessor().isSyncingBlocks();
    }

    @Override
    public boolean isBlockchainEmpty() {
        return this.getNodeBlockProcessor().getBestBlockNumber() == 0;
    }

    public void setIsPlayingBlocks(boolean value) {
        isplaying = value;
    }

    @Override
    public boolean hasBetterBlockToSync() {
            return this.getNodeBlockProcessor().hasBetterBlockToSync();
    }
}
