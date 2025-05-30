#!/usr/bin/env node

const { execSync } = require('child_process');
const os = require('os');

console.log('🛑 Stopping Gefrierschrank-App Development Services...');
console.log('====================================================');

const ports = [8080, 5173]; // Backend and Frontend ports
const platform = os.platform();

function killProcessOnPort(port) {
  try {
    let command;
    
    if (platform === 'win32') {
      // Windows
      const result = execSync(`netstat -ano | findstr :${port}`, { encoding: 'utf8' });
      const lines = result.split('\n').filter(line => line.includes(`0.0.0.0:${port}`) || line.includes(`[::]:${port}`));
      
      lines.forEach(line => {
        const parts = line.trim().split(/\s+/);
        const pid = parts[parts.length - 1];
        if (pid && !isNaN(pid)) {
          try {
            execSync(`taskkill /PID ${pid} /F`, { stdio: 'ignore' });
            console.log(`✅ Killed process ${pid} on port ${port}`);
          } catch (err) {
            console.log(`❌ Failed to kill process ${pid} on port ${port}`);
          }
        }
      });
    } else {
      // Unix-like (macOS, Linux)
      try {
        const result = execSync(`lsof -ti:${port}`, { encoding: 'utf8' });
        const pids = result.trim().split('\n').filter(pid => pid);
        
        if (pids.length > 0) {
          pids.forEach(pid => {
            try {
              execSync(`kill -9 ${pid}`, { stdio: 'ignore' });
              console.log(`✅ Killed process ${pid} on port ${port}`);
            } catch (err) {
              console.log(`❌ Failed to kill process ${pid} on port ${port}`);
            }
          });
        } else {
          console.log(`ℹ️  No process found on port ${port}`);
        }
      } catch (err) {
        console.log(`ℹ️  No process found on port ${port}`);
      }
    }
  } catch (error) {
    console.log(`ℹ️  No process found on port ${port}`);
  }
}

function killByProcessName() {
  try {
    if (platform === 'win32') {
      // Kill Java processes (Spring Boot)
      try {
        execSync('taskkill /F /IM java.exe', { stdio: 'ignore' });
        console.log('✅ Killed Java processes');
      } catch (err) {
        console.log('ℹ️  No Java processes to kill');
      }
      
      // Kill Node processes (might affect other Node apps - be careful)
      try {
        const result = execSync('tasklist /FI "IMAGENAME eq node.exe" /FO CSV', { encoding: 'utf8' });
        const lines = result.split('\n').slice(1); // Skip header
        lines.forEach(line => {
          if (line.includes('node.exe')) {
            const pid = line.split(',')[1].replace(/"/g, '');
            try {
              execSync(`taskkill /PID ${pid} /F`, { stdio: 'ignore' });
              console.log(`✅ Killed Node process ${pid}`);
            } catch (err) {
              // Ignore errors
            }
          }
        });
      } catch (err) {
        console.log('ℹ️  No Node processes to kill');
      }
    } else {
      // Unix-like: Kill Spring Boot processes
      try {
        execSync('pkill -f "spring-boot:run"', { stdio: 'ignore' });
        console.log('✅ Killed Spring Boot processes');
      } catch (err) {
        console.log('ℹ️  No Spring Boot processes to kill');
      }
      
      // Kill Vite dev server
      try {
        execSync('pkill -f "vite"', { stdio: 'ignore' });
        console.log('✅ Killed Vite processes');
      } catch (err) {
        console.log('ℹ️  No Vite processes to kill');
      }
    }
  } catch (error) {
    console.log('ℹ️  Error killing processes by name');
  }
}

// Kill processes on specific ports
ports.forEach(port => {
  console.log(`🔍 Checking port ${port}...`);
  killProcessOnPort(port);
});

// Kill by process name as backup
console.log('\n🔍 Killing processes by name...');
killByProcessName();

console.log('\n🎉 Cleanup completed!');
console.log('====================================================');
console.log('All development services have been stopped.');
console.log('');
console.log('💡 Quick commands:');
console.log('   npm run dev     - Start both services');
console.log('   ./start-dev.sh  - Start with detailed output (macOS/Linux)');
console.log('   npm run stop    - Stop all services');