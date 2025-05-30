#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

console.log('🔐 Gefrierschrank-App Security Credentials');
console.log('==========================================');

const logFile = path.join(__dirname, '..', 'backend-dev.log');

if (fs.existsSync(logFile)) {
    try {
        const logContent = fs.readFileSync(logFile, 'utf8');
        const passwordMatch = logContent.match(/Using generated security password: ([\w-]+)/);
        
        if (passwordMatch) {
            const password = passwordMatch[1];
            console.log('✅ Found security credentials:');
            console.log('');
            console.log(`👤 Username: user`);
            console.log(`🔐 Password: ${password}`);
            console.log('');
            console.log('🌐 Access URLs:');
            console.log(`   API:      http://localhost:8080/api`);
            console.log(`   H2 DB:    http://localhost:8080/h2-console`);
            console.log(`   Frontend: http://localhost:5173`);
            console.log('');
            console.log('💡 Test API access:');
            console.log(`   curl -u user:${password} http://localhost:8080/api/products`);
            console.log('');
            console.log('📊 H2 Database Connection:');
            console.log('   JDBC URL: jdbc:h2:mem:testdb');
            console.log('   Username: sa');
            console.log('   Password: (empty)');
        } else {
            console.log('❌ Could not find security password in log file');
            console.log('ℹ️  Make sure the backend is running and check backend-dev.log');
        }
    } catch (error) {
        console.log('❌ Error reading log file:', error.message);
    }
} else {
    console.log('❌ Backend log file not found');
    console.log('ℹ️  Start the application with: npm run dev or ./start-dev.sh');
}

console.log('==========================================');