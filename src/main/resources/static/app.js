//
//<script>
//    // Configuration - Update these to match your Spring Boot API endpoints
//    const API_BASE_URL = 'http://localhost:8080/api';
//
//    // Global state
//    let currentShop = null;
//    let currentStatus = 'waiting';
//    let customers = [];
//    let shops = [];
//
//    // Initialize application
//    document.addEventListener('DOMContentLoaded', function() {
//        loadShops();
//        setupEventListeners();
//        // Load data every 30 seconds
//        setInterval(loadCustomers, 30000);
//    });
//
//    // Load shops from API
//    async function loadShops() {
//        try {
//            const response = await fetch(`${API_BASE_URL}/shops`);
//            shops = await response.json();
//
//            const shopSelect = document.getElementById('shopSelect');
//            shopSelect.innerHTML = '<option value="">Select Shop Location</option>';
//
//            shops.forEach(shop => {
//                const option = document.createElement('option');
//                option.value = shop.id;
//                option.textContent = shop.name;
//                shopSelect.appendChild(option);
//            });
//        } catch (error) {
//            console.error('Error loading shops:', error);
//        }
//    }
//
//    // Setup event listeners
//    function setupEventListeners() {
//        // Shop selection
//        document.getElementById('shopSelect').addEventListener('change', function(e) {
//            currentShop = e.target.value;
//            if (currentShop) {
//                loadCustomers();
//                loadStats();
//            }
//        });
//
//        // Add customer form
//        document.getElementById('addCustomerForm').addEventListener('submit', function(e) {
//            e.preventDefault();
//            addCustomer();
//        });
//
//        // Queue tabs
//        document.querySelectorAll('.queue-tab').forEach(tab => {
//            tab.addEventListener('click', function() {
//                document.querySelectorAll('.queue-tab').forEach(t => t.classList.remove('active'));
//                this.classList.add('active');
//                currentStatus = this.dataset.status;
//                renderCustomers();
//            });
//        });
//    }
//
//    // Add new customer
//    async function addCustomer() {
//        if (!currentShop) {
//            alert('Please select a shop first');
//            return;
//        }
//
//        const customerData = {
//            name: document.getElementById('customerName').value,
//            phone: document.getElementById('customerPhone').value,
//            serviceType: document.getElementById('serviceType').value,
//            shopId: currentShop
//        };
//
//        try {
//            const response = await fetch(`${API_BASE_URL}/customers`, {
//                method: 'POST',
//                headers: {
//                    'Content-Type': 'application/json',
//                },
//                body: JSON.stringify(customerData)
//            });
//
//            if (response.ok) {
//                document.getElementById('addCustomerForm').reset();
//                loadCustomers();
//                loadStats();
//            } else {
//                alert('Error adding customer');
//            }
//        } catch (error) {
//            console.error('Error adding customer:', error);
//            alert('Error adding customer');
//        }
//    }
//
//    // Load customers from API
//    async function loadCustomers() {
//        if (!currentShop) return;
//
//        try {
//            const response = await fetch(`${API_BASE_URL}/customers/shop/${currentShop}`);
//            customers = await response.json();
//            renderCustomers();
//        } catch (error) {
//            console.error('Error loading customers:', error);
//            document.getElementById('queueContainer').innerHTML = '<div class="empty-queue">Error loading customers</div>';
//        }
//    }
//
//    // Render customers based on current status
//    function renderCustomers() {
//        const container = document.getElementById('queueContainer');
//        const filteredCustomers = customers.filter(customer => customer.status === currentStatus);
//
//        if (filteredCustomers.length === 0) {
//            container.innerHTML = `<div class="empty-queue">No customers ${currentStatus.replace('_', ' ')}</div>`;
//            return;
//        }
//
//        container.innerHTML = filteredCustomers.map((customer, index) => `
//            <div class="customer-card">
//                <div class="customer-header">
//                    <div class="customer-name">${customer.name}</div>
//                    ${currentStatus === 'waiting' ? `<div class="queue-position">#${index + 1}</div>` : ''}
//                </div>
//                <div class="customer-details">
//                    <strong>Service:</strong> ${customer.serviceType}<br>
//                    ${customer.phone ? `<strong>Phone:</strong> ${customer.phone}<br>` : ''}
//                    <strong>Added:</strong> ${new Date(customer.createdAt).toLocaleTimeString()}<br>
//                    ${customer.startedAt ? `<strong>Started:</strong> ${new Date(customer.startedAt).toLocaleTimeString()}<br>` : ''}
//                    ${customer.completedAt ? `<strong>Completed:</strong> ${new Date(customer.completedAt).toLocaleTimeString()}` : ''}
//                </div>
//                <div class="customer-actions">
//                    ${getActionButtons(customer)}
//                </div>
//            </div>
//        `).join('');
//    }
//
//    // Get action buttons based on customer status
//    function getActionButtons(customer) {
//        switch (customer.status) {
//            case 'waiting':
//                return `
//                    <button class="btn btn-success" onclick="startService(${customer.id})">Start Service</button>
//                    <button class="btn btn-danger" onclick="removeCustomer(${customer.id})">Remove</button>
//                `;
//            case 'in_service':
//                return `
//                    <button class="btn btn-primary" onclick="completeService(${customer.id})">Complete Service</button>
//                    <button class="btn btn-warning" onclick="pauseService(${customer.id})">Pause</button>
//                `;
//            case 'completed':
//                return `
//                    <button class="btn btn-success" onclick="processPayment(${customer.id})">Process Payment</button>
//                `;
//            default:
//                return '';
//        }
//    }
//
//    // Customer action functions
//    async function startService(customerId) {
//        await updateCustomerStatus(customerId, 'in_service');
//    }
//
//    async function completeService(customerId) {
//        await updateCustomerStatus(customerId, 'completed');
//    }
//
//    async function pauseService(customerId) {
//        await updateCustomerStatus(customerId, 'waiting');
//    }
//
//    async function processPayment(customerId) {
//        await updateCustomerStatus(customerId, 'paid');
//    }
//
//    async function removeCustomer(customerId) {
//        if (confirm('Are you sure you want to remove this customer?')) {
//            try {
//                const response = await fetch(`${API_BASE_URL}/customers/${customerId}`, {
//                    method: 'DELETE'
//                });
//
//                if (response.ok) {
//                    loadCustomers();
//                    loadStats();
//                } else {
//                    alert('Error removing customer');
//                }
//            } catch (error) {
//                console.error('Error removing customer:', error);
//            }
//        }
//    }
//
//    // Update customer status
//    async function updateCustomerStatus(customerId, status) {
//        try {
//            const response = await fetch(`${API_BASE_URL}/customers/${customerId}/status`, {
//                method: 'PUT',
//                headers: {
//                    'Content-Type': 'application/json',
//                },
//                body: JSON.stringify({ status })
//            });
//
//            if (response.ok) {
//                loadCustomers();
//                loadStats();
//            } else {
//                alert('Error updating customer status');
//            }
//        } catch (error) {
//            console.error('Error updating customer status:', error);
//        }
//    }
//
//    // Load and display stats
//    async function loadStats() {
//        if (!currentShop) return;
//
//        try {
//            const response = await fetch(`${API_BASE_URL}/shops/${currentShop}/stats`);
//            const stats = await response.json();
//
//            document.getElementById('queueCount').textContent = stats.queueCount || 0;
//            document.getElementById('servedCount').textContent = stats.servedCount || 0;
//            document.getElementById('avgWaitTime').textContent = `${stats.avgWaitTime || 0} min`;
//        } catch (error) {
//            console.error('Error loading stats:', error);
//        }
//    }
//</script>