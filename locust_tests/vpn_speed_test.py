from locust import User, task, between
import subprocess

class VpnLoadTest(User):
    wait_time = between(5, 10)

    @task(3)
    def connect_to_vpn(self):
        try:
            result = subprocess.run(
                ["openvpn", "--config", "my-config.ovpn"],
                capture_output=True, text=True, timeout=10
            )
            if "Initialization Sequence Completed" in result.stdout:
                print("VPN подключен успешно")
            else:
                print("Ошибка подключения:", result.stderr)
        except Exception as e:
            print(f"Ошибка: {e}")

    @task(1)
    def speed_test(self):
        try:
            result = subprocess.run(
                ["iperf3", "-c", "vpn-server-ip", "-P", "10"],
                capture_output=True, text=True, timeout=30
            )
            print(result.stdout)
        except Exception as e:
            print(f"Ошибка: {e}")
