/**
 * PixelVault Retro Gaming Engine - Frontend Application
 * Interacts with Spring Boot REST API:
 *   - GET  /api/v1/juegos/catalogo-publico
 *   - GET  /api/v1/coleccionistas
 *   - GET  /api/v1/coleccionistas/{id}/balance
 *   - POST /api/v1/ordenes/checkout
 *   - GET  /api/v1/ordenes/historial/{id}
 */

const API_BASE = '/api/v1';

// State
let catalogData = [];
let collectorsData = [];

// DOM Elements
const connectionStatusEl = document.getElementById('connectionStatus');
const vaultKeyInput = document.getElementById('vaultKey');
const catalogGrid = document.getElementById('catalogGrid');
const collectorsList = document.getElementById('collectorsList');
const coleccionistaSelect = document.getElementById('coleccionistaId');
const videojuegoSelect = document.getElementById('videojuegoId');
const historyCollectorSelect = document.getElementById('historyCollectorId');
const checkoutResult = document.getElementById('checkoutResult');
const historyList = document.getElementById('historyList');
const toastContainer = document.getElementById('toastContainer');

// Helper to get Vault Pass Header
function getHeaders(customHeaders = {}) {
    const key = vaultKeyInput ? vaultKeyInput.value.trim() : 'RETRO-MASTER-KEY-1989';
    return {
        'Content-Type': 'application/json',
        'X-Vault-Pass': key,
        ...customHeaders
    };
}

// ================= Tab Management =================
function switchTab(tabName) {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.toggle('active', btn.getAttribute('data-tab') === tabName);
    });

    document.querySelectorAll('.tab-panel').forEach(panel => {
        panel.classList.toggle('active', panel.id === `tab-${tabName}`);
    });

    if (tabName === 'catalog') loadCatalog();
    if (tabName === 'collectors') loadCollectors();
    if (tabName === 'checkout') populateCheckoutSelects();
    if (tabName === 'history') populateHistorySelect();
}

// ================= Toast Notifications =================
function showToast(message, type = 'info') {
    if (!toastContainer) return;
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    const icon = type === 'success' ? '✅' : type === 'error' ? '❌' : 'ℹ️';
    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 400);
    }, 4000);
}

// ================= Status Update =================
function setStatus(online, message) {
    if (!connectionStatusEl) return;
    connectionStatusEl.className = 'status-indicator ' + (online ? 'status-online' : 'status-offline');
    const textEl = connectionStatusEl.querySelector('.status-text');
    if (textEl) {
        textEl.textContent = online ? 'ONLINE (VAULT OPEN)' : (message || 'OFFLINE');
    }
}

// ================= Load Catalog =================
async function loadCatalog() {
    try {
        const response = await fetch(`${API_BASE}/juegos/catalogo-publico`);
        if (!response.ok) throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        
        catalogData = await response.json();
        setStatus(true);
        renderCatalog(catalogData);
        populateCheckoutSelects();
    } catch (err) {
        console.error('Error loading catalog:', err);
        setStatus(false, 'CONN ERROR');
        if (catalogGrid) {
            catalogGrid.innerHTML = `
                <div class="error-box">
                    <h3>⚠️ Error conectando al API PixelVault</h3>
                    <p>${err.message}</p>
                    <p style="margin-top: 8px; font-size: 13px; color: var(--text-secondary);">
                        Asegúrate de que Spring Boot esté corriendo en el puerto 8080.
                    </p>
                    <button class="btn-search" style="margin-top: 12px;" onclick="loadCatalog()">Reintentar</button>
                </div>
            `;
        }
    }
}

function renderCatalog(games) {
    if (!catalogGrid) return;
    if (!games || games.length === 0) {
        catalogGrid.innerHTML = '<p class="empty-msg">No hay videojuegos disponibles en la bóveda.</p>';
        return;
    }

    catalogGrid.innerHTML = games.map(game => {
        const isHolyGrail = game.rareza === 'SANTO_GRIAL';
        const price = Number(game.precio ?? game.precioBase ?? 0).toFixed(2);
        const serial = game.serialRetro || game.serialColeccion || 'N/A';
        const stockClass = game.stock > 3 ? 'stock-available' : game.stock > 0 ? 'stock-low' : 'stock-out';
        
        return `
            <div class="game-card ${isHolyGrail ? 'holy-grail-card' : ''}">
                <div class="game-card-header">
                    <h3 class="game-title">${escapeHtml(game.titulo || 'Sin título')}</h3>
                    <span class="rarity-badge rarity-${game.rareza || 'COMUN'}">
                        ${isHolyGrail ? '🏆 ' : ''}${game.rareza || 'COMUN'}
                    </span>
                </div>
                <div class="game-details">
                    <div class="detail-item">
                        <span class="detail-label">Plataforma:</span>
                        <span class="detail-value">💾 ${escapeHtml(game.plataforma || 'N/A')}</span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Año:</span>
                        <span class="detail-value">📅 ${game.anioLanzamiento || 'N/A'}</span>
                    </div>
                    <div class="detail-item" style="grid-column: span 2;">
                        <span class="detail-label">Serial Retro:</span>
                        <span class="detail-value"><code>${escapeHtml(serial)}</code></span>
                    </div>
                    <div class="detail-item" style="grid-column: span 2;">
                        <span class="detail-label">Conservación:</span>
                        <span class="detail-value">✨ ${escapeHtml(game.estadoConservacion || 'EXCELENTE')}</span>
                    </div>
                </div>
                <div class="game-footer">
                    <div class="game-price">$${price}</div>
                    <div class="game-stock ${stockClass}">${game.stock} unid.</div>
                </div>
                <button class="btn-checkout" style="margin-top: 14px; width: 100%; font-size: 9px; padding: 10px;" onclick="selectForCheckout(${game.id})" ${game.stock <= 0 ? 'disabled' : ''}>
                    ${game.stock > 0 ? '⚡ COMPRAR AHORA' : 'AGOTADO'}
                </button>
            </div>
        `;
    }).join('');
}

// ================= Load Collectors =================
async function loadCollectors() {
    try {
        const response = await fetch(`${API_BASE}/coleccionistas`, {
            headers: getHeaders()
        });

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.mensaje || `HTTP ${response.status}`);
        }

        collectorsData = await response.json();
        renderCollectors(collectorsData);
        populateCheckoutSelects();
        populateHistorySelect();
    } catch (err) {
        console.error('Error loading collectors:', err);
        if (collectorsList) {
            collectorsList.innerHTML = `
                <div class="error-box">
                    <h3>⚠️ Error cargando coleccionistas</h3>
                    <p>${err.message}</p>
                    <p style="margin-top: 8px; font-size: 13px;">Verifica la llave <strong>X-Vault-Pass</strong> en el encabezado.</p>
                </div>
            `;
        }
    }
}

function renderCollectors(collectors) {
    if (!collectorsList) return;
    if (!collectors || collectors.length === 0) {
        collectorsList.innerHTML = '<p class="empty-msg">No hay coleccionistas registrados.</p>';
        return;
    }

    collectorsList.innerHTML = collectors.map(c => `
        <div class="collector-card">
            <div class="collector-avatar">👾</div>
            <div class="collector-details">
                <div class="collector-header">
                    <h4>${escapeHtml(c.aliasGamer)}</h4>
                    <span class="collector-id">ID: #${c.id}</span>
                </div>
                <p class="collector-email">✉️ ${escapeHtml(c.email)}</p>
                <div class="collector-balance-box">
                    <span class="balance-label">Saldo en Bóveda</span>
                    <span class="balance-amount">$${Number(c.saldoBoveda || 0).toFixed(2)} PX$</span>
                </div>
                <div class="collector-actions">
                    <button class="btn-view-history" onclick="viewCollectorHistory(${c.id})">
                        📜 Ver Historial
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// ================= Selectors for Checkout & History =================
function populateCheckoutSelects() {
    if (coleccionistaSelect && collectorsData.length > 0) {
        const currentVal = coleccionistaSelect.value;
        coleccionistaSelect.innerHTML = '<option value="">-- Selecciona Coleccionista --</option>' +
            collectorsData.map(c => `
                <option value="${c.id}" ${currentVal == c.id ? 'selected' : ''}>
                    #${c.id} - ${escapeHtml(c.aliasGamer)} ($${Number(c.saldoBoveda).toFixed(2)} PX$)
                </option>
            `).join('');
    }

    if (videojuegoSelect && catalogData.length > 0) {
        const currentVal = videojuegoSelect.value;
        videojuegoSelect.innerHTML = '<option value="">-- Selecciona Videojuego --</option>' +
            catalogData.map(g => {
                const price = Number(g.precio ?? g.precioBase ?? 0).toFixed(2);
                return `
                    <option value="${g.id}" ${currentVal == g.id ? 'selected' : ''} ${g.stock <= 0 ? 'disabled' : ''}>
                        #${g.id} - ${escapeHtml(g.titulo)} (${g.rareza}) - $${price} [Stock: ${g.stock}]
                    </option>
                `;
            }).join('');
    }
}

function populateHistorySelect() {
    if (historyCollectorSelect && collectorsData.length > 0) {
        const currentVal = historyCollectorSelect.value;
        historyCollectorSelect.innerHTML = '<option value="">-- Selecciona Coleccionista --</option>' +
            collectorsData.map(c => `
                <option value="${c.id}" ${currentVal == c.id ? 'selected' : ''}>
                    #${c.id} - ${escapeHtml(c.aliasGamer)}
                </option>
            `).join('');
    }
}

function selectForCheckout(gameId) {
    switchTab('checkout');
    setTimeout(() => {
        if (videojuegoSelect) {
            videojuegoSelect.value = gameId;
        }
    }, 100);
}

function viewCollectorHistory(collectorId) {
    switchTab('history');
    setTimeout(() => {
        if (historyCollectorSelect) {
            historyCollectorSelect.value = collectorId;
            loadHistory();
        }
    }, 100);
}

// ================= Execute Checkout =================
async function executeCheckout() {
    const colId = coleccionistaSelect ? coleccionistaSelect.value : null;
    const gameId = videojuegoSelect ? videojuegoSelect.value : null;
    const btnCheckout = document.getElementById('btnCheckout');

    if (!colId || !gameId) {
        showToast('Debes seleccionar un coleccionista y un videojuego', 'error');
        return;
    }

    if (btnCheckout) {
        btnCheckout.disabled = true;
        btnCheckout.innerHTML = '⏳ PROCESANDO ADQUISICIÓN...';
    }

    if (checkoutResult) {
        checkoutResult.innerHTML = `
            <h3>📡 Respuesta de la API</h3>
            <div class="loading-spinner">
                <div class="spinner"></div>
                <p>Ejecutando pipeline de validación, IoC y AOP...</p>
            </div>
        `;
    }

    try {
        const payload = {
            coleccionistaId: Number(colId),
            videojuegoId: Number(gameId)
        };

        const response = await fetch(`${API_BASE}/ordenes/checkout`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (!response.ok) {
            // Manejado por GlobalExceptionHandler
            renderCheckoutError(response.status, data);
            showToast(data.mensaje || 'Error al procesar la compra', 'error');
        } else {
            // Éxito: 201 CREATED
            renderCheckoutSuccess(data);
            showToast('¡Adquisición procesada exitosamente en la bóveda!', 'success');
            // Recargar datos para actualizar stock y saldos
            loadCatalog();
            loadCollectors();
        }
    } catch (err) {
        console.error('Checkout error:', err);
        renderCheckoutError(0, {
            error: 'NETWORK_ERROR',
            mensaje: 'Fallo de conexión con el backend: ' + err.message
        });
        showToast('Error de red al ejecutar checkout', 'error');
    } finally {
        if (btnCheckout) {
            btnCheckout.disabled = false;
            btnCheckout.innerHTML = '🎮 EJECUTAR CHECKOUT';
        }
    }
}

function renderCheckoutSuccess(order) {
    if (!checkoutResult) return;
    const base = Number(order.precioBase || 0).toFixed(2);
    const comision = Number(order.comisionPreservacion ?? order.comisionServicio ?? 0).toFixed(2);
    const total = Number(order.precioFinal ?? order.totalCobrado ?? 0).toFixed(2);
    const collector = order.aliasGamer || order.aliasColeccionista || `ID #${order.coleccionistaId}`;
    const dateStr = order.fechaOrden ? new Date(order.fechaOrden).toLocaleString() : new Date().toLocaleString();

    checkoutResult.innerHTML = `
        <div class="response-card success">
            <div class="response-header">
                <span class="status-badge badge-success">HTTP 201 CREATED</span>
                <span class="order-id">TX: ${escapeHtml(order.codigoTransaccion || '#' + order.id)}</span>
            </div>
            <div class="response-body">
                <h4 class="order-game-title">🎉 ¡Transacción Exitosa!</h4>
                <div class="order-breakdown">
                    <div class="breakdown-row">
                        <span>🕹️ Videojuego:</span>
                        <strong>${escapeHtml(order.tituloVideojuego)}</strong>
                    </div>
                    <div class="breakdown-row">
                        <span>👾 Coleccionista:</span>
                        <strong>${escapeHtml(collector)}</strong>
                    </div>
                    <div class="breakdown-row">
                        <span>🏷️ Precio Base:</span>
                        <span>$${base}</span>
                    </div>
                    <div class="breakdown-row highlight-ioc">
                        <span>⚡ Comisión Preservación (IoC Strategy):</span>
                        <strong>+$${comision}</strong>
                    </div>
                    <div class="breakdown-row total-row">
                        <span>💰 Total Debitado:</span>
                        <strong class="total-price">$${total}</strong>
                    </div>
                    <div class="breakdown-row">
                        <span>📅 Fecha:</span>
                        <span>${dateStr}</span>
                    </div>
                    <div class="breakdown-row">
                        <span>📊 Estado:</span>
                        <span class="badge-status-completed">${escapeHtml(order.estado)}</span>
                    </div>
                </div>
                <div class="pipeline-evidence">
                    <h5>🛡️ Evidencia de Arquitectura Ejecutada:</h5>
                    <ul>
                        <li>✅ <strong>Bean Validation:</strong> Payload JSON validado correctamente</li>
                        <li>✅ <strong>Interceptor Seguridad:</strong> Cabecera <code>X-Vault-Pass</code> autorizada</li>
                        <li>✅ <strong>Interceptor Auditoría:</strong> Latencia registrada en métricas</li>
                        <li>✅ <strong>IoC & DI (@Qualifier):</strong> Estrategia de comisión inyectada</li>
                        <li>✅ <strong>AOP Anti-Scalper:</strong> Límite de Holy Grails verificado</li>
                    </ul>
                </div>
            </div>
        </div>
    `;
}

function renderCheckoutError(status, errData) {
    if (!checkoutResult) return;
    const errorType = errData.error || 'ERROR';
    const errorMsg = errData.mensaje || 'Error no especificado';
    const validationErrors = errData.erroresValidacion || [];

    checkoutResult.innerHTML = `
        <div class="response-card error">
            <div class="response-header">
                <span class="status-badge badge-error">HTTP ${status || 'ERR'} ${errorType}</span>
            </div>
            <div class="response-body">
                <h4 class="error-title">⛔ La transacción fue rechazada</h4>
                <p class="error-desc">${escapeHtml(errorMsg)}</p>

                ${validationErrors.length > 0 ? `
                    <div class="validation-errors-list">
                        <h5>Errores de Validación:</h5>
                        <ul>
                            ${validationErrors.map(e => `<li>${escapeHtml(e)}</li>`).join('')}
                        </ul>
                    </div>
                ` : ''}

                <div class="pipeline-evidence error-evidence">
                    <h5>🛡️ Mecanismo de Control Activado:</h5>
                    <p>
                        ${status === 409 ? '⚡ <strong>Aspecto AOP (@AntiScalperGrailRule):</strong> Se activó la regla que previene el acaparamiento de piezas de rareza Holy Grail (máximo 2 por coleccionista).' :
                          status === 400 ? '📋 <strong>Bean Validation / GlobalExceptionHandler:</strong> Los datos enviados no cumplen con las reglas del dominio (saldo insuficiente o stock agotado).' :
                          status === 403 || status === 401 ? '🔒 <strong>VaultPassSecurityInterceptor:</strong> Cabecera de autenticación faltante o inválida.' :
                          '💥 <strong>GlobalExceptionHandler:</strong> Excepción capturada y mapeada a DTO de error uniforme.'}
                    </p>
                </div>
            </div>
        </div>
    `;
}

// ================= Load Purchase History =================
async function loadHistory() {
    const colId = historyCollectorSelect ? historyCollectorSelect.value : null;
    if (!colId) {
        showToast('Selecciona un coleccionista primero', 'error');
        return;
    }

    if (historyList) {
        historyList.innerHTML = `
            <div class="loading-spinner">
                <div class="spinner"></div>
                <p>Consultando historial de adquisiciones...</p>
            </div>
        `;
    }

    try {
        const response = await fetch(`${API_BASE}/ordenes/historial/${colId}`, {
            headers: getHeaders()
        });

        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.mensaje || `HTTP ${response.status}`);
        }

        const orders = await response.json();
        renderHistory(orders);
    } catch (err) {
        console.error('Error loading history:', err);
        if (historyList) {
            historyList.innerHTML = `
                <div class="error-box">
                    <h3>⚠️ Error consultando historial</h3>
                    <p>${err.message}</p>
                </div>
            `;
        }
    }
}

function renderHistory(orders) {
    if (!historyList) return;
    if (!orders || orders.length === 0) {
        historyList.innerHTML = `
            <div class="result-placeholder">
                <span>📭</span>
                <p>Este coleccionista aún no tiene órdenes de compra registradas.</p>
            </div>
        `;
        return;
    }

    historyList.innerHTML = `
        <div class="history-table-container">
            <table class="history-table">
                <thead>
                    <tr>
                        <th>Transacción</th>
                        <th>Videojuego</th>
                        <th>Precio Base</th>
                        <th>Comisión (IoC)</th>
                        <th>Total</th>
                        <th>Estado</th>
                        <th>Fecha</th>
                    </tr>
                </thead>
                <tbody>
                    ${orders.map(o => {
                        const base = Number(o.precioBase || 0).toFixed(2);
                        const comision = Number(o.comisionPreservacion ?? o.comisionServicio ?? 0).toFixed(2);
                        const total = Number(o.precioFinal ?? o.totalCobrado ?? 0).toFixed(2);
                        const dateStr = o.fechaOrden ? new Date(o.fechaOrden).toLocaleDateString() : 'N/A';
                        return `
                            <tr>
                                <td><strong>${escapeHtml(o.codigoTransaccion || '#' + o.id)}</strong></td>
                                <td>${escapeHtml(o.tituloVideojuego)}</td>
                                <td>$${base}</td>
                                <td class="text-accent">+$${comision}</td>
                                <td><strong>$${total}</strong></td>
                                <td><span class="badge-status-completed">${escapeHtml(o.estado)}</span></td>
                                <td>${dateStr}</td>
                            </tr>
                        `;
                    }).join('')}
                </tbody>
            </table>
        </div>
    `;
}

// ================= Utilities =================
function escapeHtml(text) {
    if (text === null || text === undefined) return '';
    const div = document.createElement('div');
    div.textContent = String(text);
    return div.innerHTML;
}

// ================= Initialization =================
document.addEventListener('DOMContentLoaded', () => {
    if (vaultKeyInput) {
        vaultKeyInput.addEventListener('change', () => {
            showToast('Llave X-Vault-Pass actualizada', 'info');
        });
    }

    loadCatalog();
    loadCollectors();
});
