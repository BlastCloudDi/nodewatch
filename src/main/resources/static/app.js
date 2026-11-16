const API_BASE = '';

// Загрузка списка целей
async function loadTargets() {
    try {
        const response = await fetch(`${API_BASE}/targets`);
        const targets = await response.json();
        renderTargets(targets);
    } catch (error) {
        console.error('Ошибка загрузки целей:', error);
        document.getElementById('targetsContainer').innerHTML =
            '<p class="loading">Ошибка загрузки</p>';
    }
}

// Отрисовка списка целей
function renderTargets(targets) {
    const container = document.getElementById('targetsContainer');

    if (targets.length === 0) {
        container.innerHTML = '<p class="loading">Нет целей. Добавьте первую!</p>';
        return;
    }

    container.innerHTML = targets.map(target => `
        <div class="target-card">
            <div class="target-info">
                <div class="target-name">${target.name}</div>
                <div class="target-url">${target.url}</div>
                <div class="target-url">Интервал: ${target.checkIntervalSeconds} сек</div>
            </div>
            <div class="target-actions">
                <button class="btn-history" onclick="showHistory('${target.id}', '${target.name}')">
                    История
                </button>
                <button class="btn-delete" onclick="deleteTarget('${target.id}')">
                    Удалить
                </button>
            </div>
        </div>
    `).join('');
}

// Добавление цели
document.getElementById('addTargetForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('targetName').value;
    const url = document.getElementById('targetUrl').value;
    const interval = parseInt(document.getElementById('targetInterval').value);

    try {
        const response = await fetch(`${API_BASE}/targets`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name,
                url,
                checkIntervalSeconds: interval
            })
        });

        if (response.ok) {
            document.getElementById('addTargetForm').reset();
            document.getElementById('targetInterval').value = '60';
            loadTargets();
        } else {
            alert('Ошибка добавления цели');
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка соединения с сервером');
    }
});

// Удаление цели
async function deleteTarget(id) {
    if (!confirm('Удалить эту цель?')) return;

    try {
        const response = await fetch(`${API_BASE}/targets/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadTargets();
        } else {
            alert('Ошибка удаления');
        }
    } catch (error) {
        console.error('Ошибка:', error);
        alert('Ошибка соединения с сервером');
    }
}

// Показ истории
async function showHistory(targetId, targetName) {
    document.getElementById('historyTargetName').textContent = targetName;
    document.getElementById('historySection').style.display = 'block';
    document.getElementById('historyContainer').innerHTML = '<p class="loading">Загрузка...</p>';

    try {
        const response = await fetch(`${API_BASE}/targets/${targetId}/history`);
        const history = await response.json();
        renderHistory(history);
    } catch (error) {
        console.error('Ошибка загрузки истории:', error);
        document.getElementById('historyContainer').innerHTML =
            '<p class="loading">Ошибка загрузки</p>';
    }
}

// Отрисовка истории
function renderHistory(history) {
    const container = document.getElementById('historyContainer');

    if (history.length === 0) {
        container.innerHTML = '<p class="loading">История пуста</p>';
        return;
    }

    container.innerHTML = history.map(item => `
        <div class="history-item">
            <div>
                <strong>${new Date(item.checkedAt).toLocaleString('ru-RU')}</strong>
            </div>
            <div>
                <span class="${item.isUp ? 'status-up' : 'status-down'}">
                    ${item.isUp ? '✓ UP' : '✗ DOWN'}
                </span>
                ${item.responseTimeMs ? `(${item.responseTimeMs}ms)` : ''}
                ${item.statusCode ? `[${item.statusCode}]` : ''}
            </div>
        </div>
    `).join('');
}

// Закрытие истории
document.getElementById('closeHistory').addEventListener('click', () => {
    document.getElementById('historySection').style.display = 'none';
});

// Загрузка целей при старте
loadTargets();